import i18n from "@/locale/i18n";

// 获取灵感大全最深层数
export const getDepth  =  (arr) => {
  const helper = (root) => {
    if (!root) return 0;
    if (!root.children) return 1;
    let ans = 0;
    for (let i = 0; i < root.children.length; ++i) {
      ans = Math.max(1 + helper(root.children[i]), ans);
    }
    return ans;
  };
  const dummyRoot = {
    id: "DummyRoot",
    children: arr
  };
  return helper(dummyRoot);
};
// 删除children为空
export const delNodeChild = (arr) => {
  arr.forEach(item => {
    if (item.children && item.children.length) {
      delNodeChild(item.children)
    } else {
      let list = arr.filter(item => (item.children && item.children.length));
      list.length ? null : item.childrenEmpty = true;
    }
  })
  return arr
}
// 多维数组过滤
export const filterArr = (arr, parentId = undefined) => {
  return arr.reduce((prev, curr) => {
    if (parentId !== undefined) {
      Object.assign(curr, { 'parentId': parentId});
    }
    prev.push(curr);
    if (curr.children && curr.children.length) {
      prev.push(...filterArr(curr.children, curr.id));
    }
    return prev;
  }, []);
}
// 数组转树
export const arrayToTree = (arr) => {
  const rootNodes = [];
  const idMap = {};
  arr.map((node) => {
    node.children = [];
    idMap[node.id] = node;
  });
  arr.map((node) => {
    const { parentId } = node;
    if (parentId === undefined) {
      rootNodes.push(node);
    } else {
      idMap[parentId].children.push(node);
    }
  });
  return rootNodes;
}
// 递归获取第一个节点
export const getDeepNode = (list, func) => {
  for ( const node of list ) {
    if(func(node)) {
      return node
    }
    if (node.children.length) {
      const res = getDeepNode(node.children, func)
      if(res) {
        return res
      }
    }
  }
}

// 根据id查询节点
export const findItemById = (id, list) => {
  for (let i = 0; i < list.length; i++) {
    const item = list[i];
    if (item.id === id) {
      return item;
    }
    if (item.children && item.children.length > 0) {
      const foundItem = findItemById(id, item.children);
      if (foundItem) {
        return foundItem;
      }
    }
  }
  return null;
}

// 递归设置disabled
export const setItemLevel = (list, level = 1) => {
  list.forEach(item => {
    item.level = level;
    if (item.children && Array.isArray(item.children)) {
      setItemLevel(item.children, level + 1);
    }
  });
}

// 查找我的分类id
export const findMyCategoryId = (list) => {
  for (let i = 0; i < list.length; i++) {
    const item = list[i];
    if (item.title === i18n.t('mine')) {
      return item.id;
    }
    if (item.children && item.children.length > 0) {
      findMyCategoryId(item.children);
    }
  }
}
     
// 获取默认展开的id
export const findExpandId = (list, nodeId) => {
  for (let i = 0; i < list.length; i++) {
    const item = list[i];
    if (item.id === nodeId) {
      return item.parentId;
    }
    if (item.children && item.children.length > 0) {
      const parentId = findExpandId(item.children, nodeId);
      if (parentId !== undefined) {
        return parentId;
      }
    }
  }
  return undefined;
}