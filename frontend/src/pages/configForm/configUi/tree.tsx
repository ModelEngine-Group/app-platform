/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useState } from 'react';
import { Tree, Input, Menu } from 'antd';
import { PlusCircleOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons';
import { uuid } from '@/common/util';
import { Message } from '@/shared/utils/message';
import { useTranslation } from 'react-i18next';
import { cloneDeep } from 'lodash';

const TreeComponent = (props) => {
  const { t } = useTranslation();
  const { setDisabled, category, tree, updateTreeData, nodeList } = props;
  const [treeData, setTreeData] = useState([]);
  const [editingId, setEditingId] = useState(null);
  const [expandedKeys, setExpandedKeys] = useState(['']);

  const handleTreeData = (data) => {
    const newData = data.map(node => {
      if (node.children.length) {
        handleTreeData(node.children);
      }
      return node;
    });
    setTreeData(newData);
  };

  const handleExpand = (value) => {
    setExpandedKeys(value);
  };

  /**
   * 校验是否有编辑中的节点
   * @returns {boolean} 返回是否
   */
  const hasNodeEditing = () => {
    if (editingId) {
      Message({ type: 'warning', content: t('editedProcess') });
      return true;
    }
    return false;
  };

  /**
   * 获取某个节点的深度
   *
   * @param tree 树形数据
   * @param target 目标节点id
   * @param currentDepth 当前深度
   */
  const findNodeDepth = (tree, target, currentDepth = 1) => {
    for (let node of tree) {
      // 如果找到目标节点，返回当前深度
      if (node.id === target) {
        return currentDepth;
      }

      // 如果当前节点有子节点，递归查找
      if (node.children && node.children.length > 0) {
        const depth = findNodeDepth(node.children, target, currentDepth + 1);
        // 如果在子树中找到目标，返回该深度
        if (depth !== 0) {
          return depth;
        }
      }
    }
    // 如果当前层中没有找到目标，返回0
    return 0;
  }

  useEffect(() => {
    setTreeData(cloneDeep(tree));
  }, [tree]);

  // 有节点在编辑中不能关闭弹框
  useEffect(() => {
    if (editingId) {
      setDisabled(true);
      return;
    }
    setDisabled(false);
  }, [editingId]);

  const titleRender = (node) => {
    const { id, title, children, parent, depth } = node;
    const editTag = (e, id) => {
      if (hasNodeEditing()) return;
      if (hasSelectedNode(id)) {
        Message({ type: 'warning', content: t('editedSelected') });
        return;
      }
      setEditingId(id);
    }

    /**
     * 类目输入框失焦后回调
     * @param e event事件
     */
    const handleTagBlur = (e) => {
      let { value } = e.target;
      value = value.trim();
      const regex = /^[a-zA-Z0-9\u4e00-\u9fa5]+([_-][a-zA-Z0-9\u4e00-\u9fa5]+)*$/;
      if (value === '') {
        Message({ type: 'warning', content: t('categoryEmpty') });
        return;
      } else if (value === t('others')) {
        Message({ type: 'warning', content: `${t('categoryNotBe')}${t('others')}` });
        return;
      } else if (value === t('mine')) {
        Message({ type: 'warning', content: `${t('categoryNotBe')}${t('mine')}` });
        return;
      } else if (!regex.test(value)) {
        Message({ type: 'warning', content: t('categoryTips') });
        return;
      } else {
        if (!validateTitle(value, node.parent.split(':')[0], id)) {
          Message({ type: 'warning', content: t('categoryLevel') });
          return;
        }
        if (id === 'key') {
          const nodeId = uuid();
          node.id = nodeId;
          node.parent = node.parent + ':' + nodeId;
        }
        node.title = value;
        setEditingId(null);
        setTreeData([...treeData]);
        updateTreeData([...treeData]);
      }
    }

    const getNewNodeDepth = (id, depth) => {
      if (depth) {
        return depth + 1;
      }
      const result = findNodeDepth(treeData, id);
      if (result > 0) {
        return result + 1;
      }
      Message({ type: 'warning', content: t('categoryDepthError') });
      return;
    }

    const addTag = () => {
      if (hasNodeEditing()) return;
      if (children.length === 100) {
        Message({ type: 'warning', content: t('categoryLimit') });
        return;
      }
      if (depth === 10 || getNewNodeDepth(id, depth) > 10) {
        Message({ type: 'warning', content: t('categoryDepthLimit') });
        return;
      }
      if (hasInspiration(id)) {
        Message({ type: 'error', content: t('categoryExists') });
        return;
      }
      if (hasSelectedNode(id)) {
        Message({ type: 'warning', content: t('categoryAdded') });
        return;
      }
      let newNode = { id: 'key', title: '', children: [], parent: id, depth: getNewNodeDepth(id, depth)};
      setEditingId(newNode.id);
      setExpandedKeys([...expandedKeys, id])
      node.children.push(newNode);
      setTreeData([...treeData]);
    }

    /**
     * 递归查询一颗树，查询应删除的节点并删除
     * @param treeData 递归查询的树的数据
     * @param parentNode 删除节点的父节点id
     * @param id 删除节点的id
     * @returns treeData 经过递归查询后的树的数据
     */
    const handleDeleteNode = (treeData, parentNode, id) => {
      if (treeData.children.length) {
        treeData.children = treeData.children.map((childrenNode) => {
          if (childrenNode.id === parentNode && childrenNode.children.length === 1) {
            return {
              ...childrenNode,
              children: []
            }
          } else if (childrenNode.id === id) {
            return null;
          } else {
            return handleDeleteNode(childrenNode, parentNode, id);
          }
        }).filter(Boolean);
      }
      return treeData;
    };

    /**
     * 遍历最外层树，查询应删除的节点并删除
     * @param parentNode 删除节点的父节点id
     * @param id 删除节点的id
     * @returns void
     */
    const handleDelete = (parentNode, id) => {
      const newTreeData = treeData.map((data) => {
        if (data.id === parentNode && data.children.length === 1) {
          return {
            ...data,
            children: []
          }
        } else if (data.id === id) {
          return null;
        } else {
          const updateNode = handleDeleteNode(data, parentNode, id);
          return updateNode;
        }
      }).filter(Boolean);
      setTreeData(newTreeData);
      updateTreeData(newTreeData);
    };

    /**
     * 树节点下拉菜单点击删除调用方法
     * @param parent 树节点parent属性，格式：父节点id:自身id
     * @returns void
     */
    const deleteNode = (parent) => {
      if (hasNodeEditing()) return;
      const [parentNode, id] = parent.split(':');
      if (children.length) {
        Message({ type: 'warning', content: t('categoryDeletion') });
      } else if (hasInspiration(id)) {
        Message({ type: 'warning', content: t('categoryExists') });
      } else if (hasSelectedNode(id)) {
        Message({ type: 'warning', content: t('categoryDeleted') });
      } else {
        handleDelete(parentNode, id);
        Message({ type: 'success', content: t('deleteSuccess') });
      }
    }

    const hasInspiration = (value) => {
      return nodeList.includes(value);
    }

    const hasSelectedNode = (value) => {
      if (category) {
        const arr = category.split(':');
        return value === arr[arr.length - 1];
      }
      return false;
    }

    const handleDoubleClick = () => {
      setEditingId(id);
    }
    // 判断节点title是否合法
    const validateTitle = (title, parentId, id) => {
      const data = [{ id: 'root', children: treeData, title: '' }];
      const node = findTreeNodeById(data, parentId);
      const { children } = node;
      return !children.some(item => item.title === title && item.id !== id);
    }
    const findNode = (tree, id) => {
      if (tree.id === id) {
        return tree;
      }
      if (tree.children) {
        for (const node of tree.children) {
          const result = findNode(node, id);
          if (result) {
            return result;
          }
        }
      }
      return null;
    }
    // 根据id查找node
    const findTreeNodeById = (treeData, id) => {
      for (const tree of treeData) {
        const node = findNode(tree, id);
        if (node) {
          return node;
        }
      }
      return null;
    }

    const dropItems = [
      { key: 'add', icon: <PlusCircleOutlined />, onClick: (e) => addTag(), label: t('addClassification') },
      { key: 'edit', icon: <EditOutlined />, onClick: (e) => editTag(e, id), label: t('editClassification') },
      { key: 'delete', icon: <DeleteOutlined />, onClick: (e) => deleteNode(parent), label: t('delete') },
    ];

    const dropMenu = (
      <Menu items={dropItems} />
    );

    return (
      <div>
        {node.id === editingId ? (
          <Input
            autoFocus
            defaultValue={title}
            onBlur={handleTagBlur}
            onPressEnter={handleTagBlur}
            maxLength={20}
          />
        ) : (
              <div className='drawer-tree-node'>
                <span onDoubleClick={handleDoubleClick}>{node.title}</span>
                <div className='drawer-tree-btn'>
                  <span onClick={(e) => addTag()}><PlusCircleOutlined style={{ fontSize: '16px' }}/></span>
                  <span onClick={(e) => editTag(e, id)}><EditOutlined style={{ fontSize: '16px' }}/></span>
                  <span onClick={(e) => deleteNode(parent)}><DeleteOutlined style={{ fontSize: '16px' }}/></span>
                </div>
              </div>
          )}
      </div>
    );
  };

  const createTagTree = () => {
    if (hasNodeEditing()) return;
    if (treeData.length === 100) {
      Message({ type: 'warning', content: t('categoryLimit') });
      return;
    }
    const id = uuid();
    let length = treeData.length;
    const newTree = {
      id,
      title: `${t('addClassification')}${length}`,
      children: [],
      parent: 'root:' + id,
      depth: 1,
    }
    setEditingId(id);
    setTreeData([...treeData, newTree]);
    updateTreeData([...treeData, newTree]);
  };

  return (
    <>
      <div className='drawer-tree'>
        <div className='drawer-tree-add' onClick={createTagTree}>
          <PlusCircleOutlined style={{ color: '#2673e5', fontSize: '16px' }}/>
          <span className='drawer-tree-text'>{t('addClassification')}</span> 
        </div>
        <Tree
          treeData={treeData}
          titleRender={titleRender}
          fieldNames={{
            title: 'title',
            key: 'id'
          }}
          showIcon
          showLine
          defaultExpandAll={true}
          expandedKeys={expandedKeys}
          onExpand={handleExpand}
        />
      </div>
    </>
  );
};

export default TreeComponent;
