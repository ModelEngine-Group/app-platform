/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useState } from 'react';
import { Collapse, Button, Divider, Tag } from 'antd';
import { getPluginDetail, getChatbotPluginDetail } from '@/shared/http/plugin';
import { useTranslation } from 'react-i18next';
import { validate } from '../utils';
import { deepClone } from '../../chatPreview/utils/chat-process';
import { ToolType } from './model';
import knowledgeImg from '@/assets/images/knowledge/plugin.png';
import userImg from '@/assets/images/ai/user.jpg';
import aiImg from '@/assets/images/ai/2.png';
import '../styles/tool-table.scss';

/**
 * 点击折叠面板获取详情
 * @param pluginData 折叠面板详情数据
 * @param toolsConfirm 工具确定
 * @param type 插件工具弹窗状态
 * @param toolAdd 工具添加方法
 * @param workflowAdd 工具流添加方法
 * @param checkedList 添加工具列表数据
 * @param modalType 工作流不同类型弹窗状态
 * @param setShowModal 工具弹窗显示隐藏方法
 * @param searchName 搜索插件名称
 */
const ToolTable = (props: any) => {
  const { t } = useTranslation();
  const {
    pluginData,
    toolsConfirm,
    type,
    toolAdd,
    workflowAdd,
    checkedList,
    modalType,
    setShowModal,
    checkData,
    searchName,
  } = props;
  const [getPluginData, setGetPluginData] = useState<any>([]);
  const modalTypes = ['pluginButtonTool', 'llmTool', 'loop'];
  let checkedToolList: any = [];

  const confirm = (item: any) => {
    const pluginInfo = pluginData?.mapType ? [pluginData] : getPluginData;
    pluginInfo.forEach((ite: any) => {
      if (ite.uniqueName === item) {
        ite.isChecked = true;
        checkedList.current.push(ite);
        if (toolsConfirm) {
          toolsConfirm(checkedList.current);
          if (ite.isChecked) {
            ite.toolCount++;
          }
          return;
        }
        if (type !== 'addSkill') {
          if (!validate('toolInvokeNodeState', checkedList.current.length)) {
            return;
          }
          toolAdd();
        } else {
          workflowAdd();
        }
        if (ite.isChecked) {
          ite.toolCount++;
        }
        if (type !== 'addSkill') {
          checkedList.current = [];
        }
      }
    });
    let newData = deepClone(pluginInfo);
    setGetPluginData(newData);
    if (modalTypes.includes(modalType)) {
      setShowModal(false);
      searchName.current = '';
    }
  };

  // 通用HTML
  const fncHTML = (item: any, toolType: string) => {
    return (
      <div className='tool-table'>
        <div className='tool-table-header'>
          <div className='tags-icon'>
            {item?.tags?.includes('HUGGINGFACE') ? (
              <img src={aiImg} />
            ) : (
              <img src={knowledgeImg} />
            )}
          </div>
          <div className='tool-table-content'>
            <div>
              <div>
                <span className='plugin-tool-name'>{item?.name || item?.pluginName}</span>
                {(toolType === ToolType.TOOL || toolType === ToolType.WATERFLOW) && (
                  <>
                    <span className='tool-version'>
                      <span className='tool-version-border tool-version-bg'>{item.version}</span>
                    </span>
                    <Divider type='vertical' />
                    <span>
                      {item.tags?.map((tag: string, index: number) => {
                        if (tag.trim().length > 0) {
                          return (
                            <Tag className='tag-position' key={index}>
                              {tag}
                            </Tag>
                          );
                        }
                      })}
                    </span>
                  </>
                )}
              </div>
              <div className='user'>
                <img width="18" height="18" src={userImg} alt="" />
                <span className='user-creator'>{pluginData.creator}</span>
              </div>
              <div className='user-description'>
                {toolType === 'panel' ? item.extension.description : item.description}
              </div>
            </div>
            {(toolType === ToolType.TOOL || toolType === ToolType.WATERFLOW) && (
              <div>
                <Button
                  disabled={type === 'addSkill' ? item.isChecked : false}
                  onClick={() => confirm(item.uniqueName)}
                >
                  {item.isChecked ? (
                    <>
                      {type !== 'addSkill' ? (
                        <>
                          <span className='add-fontsize'>{t('additions')}</span>
                          <span className='add-bgc'>+{item.toolCount}</span>
                        </>
                      ) : (
                        <span>{t('added')}</span>
                      )}
                    </>
                  ) : (
                    t('additions')
                  )}
                </Button>
              </div>
            )}
          </div>
        </div>
      </div>
    );
  };

  const onChange = async (e: any) => {
    let param = e.toString();
    if (type === 'addSkill') {
      checkData.forEach((item: any) => {
        checkedToolList.push(item.name);
      });
    }
    try {
      if (param !== '') {
        const res: any = pluginData.appCategory
          ? await getChatbotPluginDetail(param)
          : await getPluginDetail(param);
        if (res.code === 0) {
          let newRes = pluginData.appCategory ? [res.data] : res.data.pluginToolDataList;
          newRes.forEach((ite: any) => {
            if (checkedToolList.includes(ite.name)) {
              ite.isChecked = true;
            } else {
              ite.isChecked = false;
            }
            ite.toolCount = 0;
          });
          setGetPluginData(newRes);
        }
      }
    } catch {}
  };

  return (
    <>
      <Collapse expandIconPosition='end' onChange={onChange}>
        <Collapse.Panel
          header={fncHTML(pluginData, 'panel')}
          key={pluginData.appCategory ? pluginData.uniqueName : pluginData.pluginId}
        >
          {getPluginData.map((item: any, index: any) => {
            return (
              <div key={index}>
                {fncHTML(item, 'tool')}
                {getPluginData.at(-1) !== item && <Divider />}
              </div>
            );
          })}
        </Collapse.Panel>
      </Collapse>
    </>
  );
};

export default ToolTable;
