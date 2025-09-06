/*************************************************此处为人工表单示例***************************************************/
/*********************************************data为表单的初始化入参数据***********************************************/
/*******************************************terminateClick为调用终止对话接口的回调方法**********************************/
/**************************************resumingClick为调用继续对话接口的回调方法***************************************/
/***************************************restartClick为调用重新对话接口的回调方法**************************************/
import { Button } from 'antd';
import React, {useContext} from 'react';
import { DataContext } from "../context";
import '../styles/form.scss';

const SmartForm: React.FC = () => {
    const { resumingClick } = useContext(DataContext);

    // 调用继续会话接口
    const onResumeClick = (str: string) => {
        resumingClick({
            params: {
                isCvFile: str
            }
        });
    }

    return (
        <div className="form-wrap">
            <div className="question-item">
                经过分析，此文件内容可能不包含简历信息，请确认是否继续生成面试计划？
            </div>
            <div className="button-group">
                <Button onClick={() => onResumeClick('否')} style={{ marginLeft: '0'}}
                        className='save-button'>否</Button>
                <Button onClick={() => onResumeClick('是')} type="primary"
                        className='save-button'>是</Button>
            </div>
        </div>
    );
};

export default SmartForm;