import React, { useContext, useEffect, useState } from 'react';
import { Button, Table, Card, Typography, Space, Tooltip, Modal, Input, message, Dropdown, Menu } from 'antd';
import {
    PlusOutlined,
    EllipsisOutlined,
    ExclamationCircleOutlined,
    LogoutOutlined
} from '@ant-design/icons';
import { DataContext } from '../context';
import '../styles/form.scss';

const { Title, Text } = Typography;
const { confirm } = Modal;

const SmartForm: React.FC = () => {
    const { data, resumingClick } = useContext(DataContext);
    const [modelList, setModelList] = useState([]);
    const [isModalVisible, setIsModalVisible] = useState(false);
    const [newModel, setNewModel] = useState({ modelName: '', baseUrl: '', apiKey: '' });

    const buildOutputInfo = (partial: Partial<{
        modelName: string;
        modelId: string;
        baseUrl: string;
        userId: string;
        isDefault: number;
        apiKey: string;
    }>) => ({
        modelName: partial.modelName || '',
        modelId: partial.modelId || '',
        baseUrl: partial.baseUrl || '',
        userId: partial.userId || '',
        isDefault: partial.isDefault ?? 0,
        apiKey: partial.apiKey || ''
    });

    useEffect(() => {
        if (Array.isArray(data?.models)) {
            const enhancedModels = data.models.map((item, index) => ({
                ...item,
                serial: index + 1,
            }));
            setModelList(enhancedModels);
        }
    }, [data]);

    const handleAdd = () => setIsModalVisible(true);
    const handleAddCancel = () => setIsModalVisible(false);

    const handleAddConfirm = () => {
        const { modelName, baseUrl, apiKey } = newModel;
        if (!modelName.trim() || !baseUrl.trim() || !apiKey.trim()) {
            message.warning('请填写完整模型信息');
            return;
        }

        const userId = data?.models?.[0]?.userId || '';

        const payload = buildOutputInfo({
            userId,
            modelName,
            baseUrl,
            apiKey
        });

        resumingClick({
            params: {
                action: 'add',
                info: payload
            }
        });

        setIsModalVisible(false);
        setNewModel({ modelName: '', baseUrl: '', apiKey: '' });
    };

    const handleDelete = (record: any) => {
        confirm({
            title: '删除确认',
            icon: <ExclamationCircleOutlined />,
            content: (
                <>
                    <p>你确定要删除这个模型吗？</p>
                    <p><strong>模型名称：</strong>{record.modelName}</p>
                </>
            ),
            okText: '确认删除',
            cancelText: '取消',
            okType: 'danger',
            onOk: () => {
                const payload = buildOutputInfo({
                    ...record,
                    apiKey: ''
                });
                resumingClick({
                    params: {
                        action: 'delete',
                        info: payload
                    }
                });
            },
        });
    };

    const handleSwitchDefault = (record: any) => {
        confirm({
            title: '切换默认模型',
            icon: <ExclamationCircleOutlined />,
            content: `是否将 "${record.modelName}" 设置为默认模型？`,
            okText: '确认',
            cancelText: '取消',
            onOk: () => {
                const payload = buildOutputInfo({
                    ...record,
                    apiKey: ''
                });
                resumingClick({
                    params: {
                        action: 'switch',
                        info: payload
                    }
                });
            }
        });
    };

    const handleExit = () => {
        const payload = buildOutputInfo({})
        resumingClick({
            params: {
                action: 'quit',
                info: payload
            }
        });
    };

    const columns = [
        {
            title: 'ID',
            dataIndex: 'serial',
            key: 'serial',
            align: 'center',
            width: 60,
        },
        {
            title: '模型名称',
            dataIndex: 'modelName',
            key: 'modelName',
            align: 'center',
            render: (text: string) => (
                <Tooltip title={text}>
                    <div
                        style={{
                            maxWidth: '300px',
                            whiteSpace: 'nowrap',
                            overflow: 'hidden',
                            textOverflow: 'ellipsis',
                            margin: '0 auto',
                        }}
                    >
                        {text}
                    </div>
                </Tooltip>
            ),
        },
        {
            title: 'Base URL',
            dataIndex: 'baseUrl',
            key: 'baseUrl',
            align: 'center',
            render: (text: string) => (
                <Tooltip title={text}>
                    <div
                        style={{
                            maxWidth: '300px',
                            whiteSpace: 'nowrap',
                            overflow: 'hidden',
                            textOverflow: 'ellipsis',
                            margin: '0 auto',
                        }}
                    >
                        {text}
                    </div>
                </Tooltip>
            ),
        },
        {
            title: '是否默认',
            dataIndex: 'isDefault',
            key: 'isDefault',
            align: 'center',
            render: (value: number) => (value === 1 ? '是' : '否'),
        },
        {
            title: '操作',
            key: 'action',
            align: 'center',
            width: 80,
            render: (_: any, record: any) => {
                const menuItems = [
                    record.isDefault !== 1 && {
                        key: 'switch',
                        label: '设为默认模型',
                        onClick: () => handleSwitchDefault(record)
                    },
                    {
                        key: 'delete',
                        label: '删除模型',
                        onClick: () => handleDelete(record)
                    }
                ].filter(Boolean);

                return (
                    <Dropdown overlay={<Menu items={menuItems} />} trigger={['click']}>
                        <Button icon={<EllipsisOutlined />} />
                    </Dropdown>
                );
            }
        },
    ];

    return (
        <div className="form-wrap form-container">
            <Card bordered={false} style={{ maxWidth: 1200, margin: '0 auto' }}>
                <Title level={3} style={{ textAlign: 'center' }}>模型管理</Title>
                <Text type="secondary" style={{ display: 'block', textAlign: 'center', marginBottom: 24 }}>
                    以下是系统中已注册的模型信息，可进行添加或退出。
                </Text>

                <Table
                    columns={columns}
                    dataSource={modelList}
                    rowKey="serial"
                    pagination={false}
                    bordered
                    size="middle"
                    locale={{ emptyText: '暂无模型数据，请点击"添加模型"' }}></Table>

                <div style={{ marginTop: 32, textAlign: 'center' }}>
                    <Space size="large">
                        <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>添加模型</Button>
                        <Button icon={<LogoutOutlined />} onClick={handleExit}>退出</Button>
                    </Space>
                </div>
            </Card>

            <Modal
                title="添加新模型"
                open={isModalVisible}
                onOk={handleAddConfirm}
                onCancel={handleAddCancel}
                okText="确认"
                cancelText="取消"
            >
                <div style={{ marginBottom: 12 }}>
                    <label>模型名称：</label>
                    <Input
                        maxLength={100}
                        showCount
                        value={newModel.modelName}
                        onChange={(e) => setNewModel({ ...newModel, modelName: e.target.value })}
                        placeholder="请输入模型名称"
                    />
                </div>
                <div style={{ marginBottom: 12 }}>
                    <label>API Key：</label>
                    <Input
                        value={newModel.apiKey}
                        onChange={(e) => setNewModel({ ...newModel, apiKey: e.target.value })}
                        placeholder="请输入 API Key"
                    />
                </div>
                <div>
                    <label>Base URL：</label>
                    <Input
                        maxLength={200}
                        showCount
                        value={newModel.baseUrl}
                        onChange={(e) => setNewModel({ ...newModel, baseUrl: e.target.value })}
                        placeholder="请输入 Base URL"
                    />
                </div>
            </Modal>
        </div>
    );
};

export default SmartForm;
