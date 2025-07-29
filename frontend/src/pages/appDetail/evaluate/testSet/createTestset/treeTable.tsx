/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useState } from 'react';
import { Button, Tree, Checkbox, Pagination } from 'antd';
import Paginations from '@/components/pagination';
import { deleteEvalData } from '@/shared/http/appEvaluate';
import EmptyItem from '@/components/empty/empty-upload';
import { useTranslation } from 'react-i18next';

interface Props {
  data?: any;
  treeDatas?: any;
  type?: string;
  setGetIndex?: any;
  setGetPage?: any;
  total?: number;
  show?: boolean;
  refreshTestsetData?: any;
}

const TreeTable = ({
  data,
  treeDatas,
  type,
  setGetIndex,
  setGetPage,
  total,
  show,
  refreshTestsetData,
}: Props) => {
  const { t } = useTranslation();
  const [treeData, setTreeData] = useState([]);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [isAllChecked, setIsAllChecked] = useState(false);
  const [isIndeterminate, setIsIndeterminate] = useState(false);
  const [list, setList] = useState([]);
  const [delIdParams, setDelIdParams] = useState([]);
  const [check, setCheck] = useState(false);

  useEffect(() => {
    treeFnc(data);
  }, [data]);

  const treeDataFn = (item: any, obj: any, index: string) => {
    delete item.content;
    Object.keys(item).forEach((key) => {
      let objKey = {
        title: `${key}：${item[key]}`,
        key: `${key}-${index}`,
      };
      if (typeof item[key] === 'object') {
        objKey.children = [];
        objKey.title = key;
        treeDataFn(item[key], objKey, `${key}-${index}`);
      }
      obj.children.push(objKey);
    });
  };

  // 初始化数据二次封装
  const treeFnc = (data: any) => {
    const newData = JSON.parse(JSON.stringify(data));
    let newArr: any = [];
    if (type === 'create' || show) {
      newData?.contents?.map((item: string, index: any) => {
        let itemContent = JSON.parse(item);
        newArr.push(itemContent);
        return newArr;
      });
    } else {
      newData?.map((ite: any) => {
        let iteContent = JSON.parse(ite.content);
        iteContent.content = ite.id;
        newArr.push(iteContent);
        return newArr;
      });
    }
    let treeList: any = [];
    newArr.forEach((item: any, index: string) => {
      let title = type === 'create' ? index + 1 : (page - 1) * pageSize + index + 1;
      let keys = type === 'create' ? index + 1 : item.content;
      let obj = {
        title: title,
        key: keys,
        children: [],
        parent: true,
        checked: false,
        keyIndex: index,
      };
      treeDataFn(item, obj, index);
      treeList.push(obj);
    });
    setTreeData(treeList);
    setList(arrSplit(treeList, page, pageSize));
    type === 'detail' ? null : treeDatas(treeList);
  };

  const paginationChange = (curPage: number, curPageSize: number) => {
    setPage(curPage);
    setGetIndex(curPage);
    setPageSize(curPageSize);
    setGetPage(curPageSize);
  };

  const checkedLength = (val: any[]) => {
    const filter = val.filter((item: any) => item.checked);
    if (filter.length === 0) {
      setIsAllChecked(false);
      setIsIndeterminate(false);
    } else if (filter.length === val.length) {
      setIsAllChecked(true);
      setIsIndeterminate(false);
    } else {
      setIsAllChecked(false);
      setIsIndeterminate(true);
    }
    let checkedId: any = [];
    filter.map((item) => {
      checkedId.push(item.key);
      return checkedId;
    });
    setDelIdParams(checkedId);
  };

  const checkTree = (title: any, value: boolean) => {
    setCheck(value);
    treeData.forEach((item: any) => {
      if (item.title === title) {
        item.checked = value;
      }
    });
    checkedLength(treeData);
    setTreeData(treeData);
    setList(treeData);
    handlePaginationChange(page, pageSize);
  };

  const checkAll = (e: any) => {
    let checkAllId: any = [];
    treeData.forEach((item: any) => {
      item.checked = e.target.checked;
      checkTree(item.title, item.checked);
      checkAllId.push(item.key);
    });
    setDelIdParams(checkAllId);
    setIsAllChecked(e.target.checked);
    setIsIndeterminate(false);
    setTreeData(JSON.parse(JSON.stringify(treeData)));
    setList(treeData);
    handlePaginationChange(page, pageSize);
  };

  const handleDel = async () => {
    setIsIndeterminate(false);
    setIsAllChecked(false);
    if (type !== 'edit' || show) {
      const delCheck = treeData.filter((item: any) => !item.checked);
      setTreeData(delCheck);
      setList(delCheck);
      setCheck(false);
      if (delCheck.length % pageSize === 0 && page > 1) {
        setPage(page - 1);
      }
      type === 'detail' ? null : treeDatas(delCheck);
    } else {
      const res: any = await deleteEvalData(delIdParams);
      if (res.code === 0) {
        refreshTestsetData();
      }
    }
  };

  // 前端分页分割数据
  const arrSplit = (arr: any, index: number, size: number) => {
    const splitIndex = (index - 1) * size;
    const splitArr =
      splitIndex + size >= arr.length
        ? arr.slice(splitIndex, arr.length)
        : arr.slice(splitIndex, splitIndex + size);
    return splitArr;
  };

  const handlePaginationChange = (curPage: number, curPageSize: number) => {
    setPage(curPage);
    setPageSize(curPageSize);
    const listArr = arrSplit(treeData, curPage, curPageSize);
    setList(listArr);
  };

  useEffect(() => {
    handlePaginationChange(page, pageSize);
  }, [treeData.length, page, pageSize]);

  const frontPagination = (
    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
      {treeData.length > 0 && (
        <>
          <span>
            {t('total')}
            {treeData.length}
            {t('piece')}
          </span>
          <Pagination
            defaultCurrent={page}
            showSizeChanger
            showQuickJumper
            total={treeData.length}
            pageSize={pageSize}
            onChange={handlePaginationChange}
          />
        </>
      )}
    </div>
  );

  const rearPagination = (
    <Paginations total={total} current={page} onChange={paginationChange} pageSize={pageSize} />
  );
  return (
    <>
      {data.length !== 0 ? (
        <div>
          <div>{t('useCase')}</div>
          {type !== 'detail' ? (
            <div style={{ marginLeft: '28px' }}>
              <Checkbox checked={isAllChecked} indeterminate={isIndeterminate} onChange={checkAll}>
                {t('selectAll')}
              </Checkbox>
              <span>
                <Button disabled={!check} onClick={handleDel}>
                  {t('delete')}
                </Button>
              </span>
            </div>
          ) : (
            ''
          )}
          <Tree
            treeData={type === 'create' ? list : show ? list : treeData}
            titleRender={({ parent, title, checked }) => (
              <>
                {parent && type !== 'detail' ? (
                  <Checkbox checked={checked} onChange={(e) => checkTree(title, e.target.checked)}>
                    {title}
                  </Checkbox>
                ) : (
                  <span>{title}</span>
                )}
              </>
            )}
          />
          {type === 'create' ? frontPagination : show ? frontPagination : rearPagination}
        </div>
      ) : (
        <EmptyItem />
      )}
    </>
  );
};

export default TreeTable;
