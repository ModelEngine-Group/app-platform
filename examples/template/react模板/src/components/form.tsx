/*************************************************此处为人工表单示例***************************************************/
/*********************************************data为表单的初始化入参数据***********************************************/
/*******************************************terminateClick为调用终止对话接口的回调方法**********************************/
/**************************************resumingClick为调用继续对话接口的回调方法***************************************/
/***************************************restartClick为调用重新对话接口的回调方法**************************************/
import {Button, Form, Input, Select} from 'antd';
import React, {useContext, useEffect} from 'react';
import {DataContext} from "../context";
import '../styles/form.scss';

const SmartForm: React.FC = () => {
    const {data, terminateClick, resumingClick, restartClick} = useContext(DataContext);
    const [form] = Form.useForm();

    // 初始化表单数据
    useEffect(() => {
        if (!data) return;
        form.setFieldsValue(data);
    }, [data])

    // 调用继续会话接口
    const onResumeClick = () => {
        resumingClick({params: form.getFieldsValue()});
    }

    // 调用重新对话接口
    const onRestartClick = () => {
        restartClick({params: form.getFieldsValue()});
    }

    // 调用终止对话接口
    const onTerminateClick = () => {
        terminateClick({content: "终止会话"});
    }

    return (
        <div className="form-wrap">
            <Form
                form = {form}
                labelCol={{span: 4}}
                wrapperCol={{span: 14}}
                layout="horizontal"
            >
                <Form.Item label="image">
                <img src="./src/assets/images/empty.png" alt="" height="100px" width="100px"/>
                </Form.Item>
                <Form.Item label="a" name="a">
                    <Input/>
                </Form.Item>
                <Form.Item label="b" name="b">
                    <Select>
                        <Select.Option value="demo1">Demo1</Select.Option>
                        <Select.Option value="demo2">Demo2</Select.Option>
                    </Select>
                </Form.Item>
                <Form.Item label="button">
                <div className="form-button-list">
                    <Button onClick={onResumeClick}>继续对话</Button>
                    <Button onClick={onRestartClick}>重新对话</Button>
                    <Button onClick={onTerminateClick}>终止对话</Button>
                </div>
                </Form.Item>
            </Form>
        </div>
    );
};

export default SmartForm;