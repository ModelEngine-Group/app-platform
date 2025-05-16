/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useState, useRef, useImperativeHandle } from 'react';
import { Form, Input } from 'antd';
import { useTranslation } from 'react-i18next';
import DeleteImg from '@/assets/images/delete_btn.svg';
import '../styles/recommends.scss';

const Recommend = (props) => {
  const { t } = useTranslation();
  const { updateData, recommendValues, recommendRef, updateRecommendNum, readOnly } = props;
  const [list, setList] = useState([]);
  const listCrrent = useRef([]);
  const recommendRender = useRef(false);

  const handleDeleteIns = (index) => {
    if (recommendValues.showRecommend) {
      listCrrent.current.splice(index, 1);
      setList([...listCrrent.current]);
      saveRecommend();
    }
  };
  useImperativeHandle(recommendRef, () => {
    return { addRecommend };
  });
  const addRecommend = () => {
    listCrrent.current = [...listCrrent.current, { value: '', isChange: false }]
    setList(listCrrent.current);
  };
  const handleChange = (val, index) => {
    listCrrent.current[index].value = val;
    listCrrent.current[index].isChange = true;
    setList([...listCrrent.current]);
  };

  const handleBlur = (index) => {
    if (listCrrent.current[index].isChange) {
      saveRecommend();
    }
  };
  // 判断列表数据是否变化
  const recommondEqual = (a, b) => {
    if (a === b) return true;
    if (a == null || b == null) return false;
    if (a.length !== b.length) return false;
    for (let i = 0; i < a.length; i++) {
      if (a[i] !== b[i]) return false;
    }
    return true;
  }
  const saveRecommend = () => {
    let list = listCrrent.current.map(item => item.value.trim());
    let arr = list.filter(item => item.length);
    !recommondEqual(arr, recommendValues.list) && updateData({ ...recommendValues, list: arr });
  }
  useEffect(() => {
    if (recommendValues.list && !recommendRender.current) {
      setList(() => {
        listCrrent.current = JSON.parse(JSON.stringify(recommendValues.list)).map(item => {
          return {
            value: item,
            isChange: false
          }
        })
        return listCrrent.current;
      });
      recommendRender.current = true;
    };
  }, [props.recommendValues]);
  useEffect(() => {
    updateRecommendNum(list.length);
  }, [list]);
  return <>{(
    <div className='control-container'>
      <div className='control'>
        <div className='control-header '>
          <div className='control-title'>
            <span>{t('recommendedTips')}</span>
          </div>
        </div>
        {list.length !== 0 ? <Form.Item
          name='recommend'
          style={{
            marginTop: '10px',
            display: 'block',
          }}
        >
          {
            list.map((item, index) => {
              return (
                <div className='recommend-container' key={index}>
                  <div className='card-title'>
                    <span className='left'>
                      {t('question')} {index + 1}
                    </span>
                    <span className='right'>
                      <img src={DeleteImg} alt="" onClick={() => handleDeleteIns(index)} className={[recommendValues.showRecommend ? '' : 'not-allowed', readOnly ? 'version-preview' : ''].join(' ')} />
                    </span>
                  </div>
                  <div className='card-prompt'>
                    <Input placeholder={t('plsEnter')}
                      disabled={!recommendValues.showRecommend || readOnly}
                      value={item.value}
                      showCount
                      maxLength={300}
                      onChange={(e) => handleChange(e.target.value, index)}
                      onBlur={() => handleBlur(index)}
                    />
                  </div>
                </div>
              )
            })
          }
        </Form.Item> : ''}
      </div>
    </div>
  )}</>
};


export default Recommend;
