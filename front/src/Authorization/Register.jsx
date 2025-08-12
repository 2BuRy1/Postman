import React from 'react';
import {GithubOutlined, GoogleOutlined, LockOutlined, UserOutlined} from '@ant-design/icons';
import { Button,  Form, Input} from 'antd';
import AuthButton from "./AuthButton";



const Register = () => {
    const onFinish = values => {
        console.log('Received values of form: ', values);
        const data = {
            headers: {
                'Content-Type': 'application/json'
            },
                 'method' : 'POST',
            body:  JSON.stringify(values)
        }

        fetch('http://localhost:8080/form-registration', data)
            .then(res => res.json()).then((res) => {
                console.log(res);
        })
    };
    return (
        <div className="login">
            <div className="form-login">
                <Form
                    name="login"
                    initialValues={{ remember: true }}
                    style={{ maxWidth: 360 }}
                    onFinish={onFinish}
                >
                    <Form.Item
                        name="username"
                        rules={[{ required: true, message: 'Please input your Username!' }]}
                    >
                        <Input prefix={<UserOutlined />} placeholder="Username" />
                    </Form.Item>
                    <Form.Item
                        name="password"
                        rules={[{ required: true, message: 'Please input your Password!' }]}
                    >
                        <Input prefix={<LockOutlined />} type="password" placeholder="Password" />
                    </Form.Item>


                    <Form.Item>
                        <Button block type="primary" htmlType="submit">
                            Register
                        </Button>

                    </Form.Item>
                </Form>
            </div>


        </div>
    );
};
export default Register;