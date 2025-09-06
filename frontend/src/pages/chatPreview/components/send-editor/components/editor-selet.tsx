/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useState } from 'react';
import { Checkbox } from 'antd';

// 灵感大全下拉
const EditorSelect = (props) => {
  const { chatSelectItem, chatSelectDom, positionConfig, clearMove } = props;
  const [selectStyle, setSelectStyle] = useState({});
  const [checkedList, setCheckedList] = useState([]);
  const [checkedNameList, setCheckedNameList] = useState([]);
  useEffect(() => {
    if (chatSelectItem.multiple) {
      chatSelectItem.options.forEach((item, index) => {
        if (typeof (item) === 'string') {
          let obj = {
            question: item,
            answer: ''
          };
          chatSelectItem.options[index] = obj;
        }
      })
    }
    const { left, top, width } = positionConfig;
    const styleObj = {
      left: `${left - (200 - width) / 2}px`,
      bottom: `${document.documentElement.clientHeight - top + 10}px`,
      display: 'block',
    };
    setSelectStyle(styleObj);
  }, [props]);

  // 选项点击
  function selectClick(item) {
    if (!chatSelectItem.multiple) {
      chatSelectDom.innerText = item;
      clearMove();
    }
  }
  // 多选
  function onChange(e, item) {
    let arr = [];
    let nameArr = [];
    if (e.target.checked) {
      arr = [...checkedList, item];
    } else {
      arr = checkedList.filter((cItem) => cItem.question !== item.question);
    }
    nameArr = arr.map((item) => {
      return item.question;
    });
    setCheckedList(arr);
    setCheckedNameList(nameArr);
  }
  function stopClick(e) {
    e.stopPropagation();
  }
  return (
    <>
      {
        <div
          style={selectStyle}
          className='chat-select-content'
          onClick={stopClick}
        >
          {chatSelectItem.options.map((item, index) => {
            return (
              <div
                className='select-inner-item'
                key={index}
                onClick={selectClick.bind(this, item)}
              >
                {chatSelectItem.multiple ? (
                  <Checkbox
                    checked={checkedNameList.includes(item.question)}
                    onChange={(e) => onChange(e, item)}
                  >
                    <span className='check-span' title={item.question}>
                      {item.question}
                    </span>
                  </Checkbox>
                ) : (
                    <span className='normal-span' title={item}>
                      {item}
                    </span>
                  )}
              </div>
            );
          })}
        </div>
      }
    </>
  );
};

export default EditorSelect;
