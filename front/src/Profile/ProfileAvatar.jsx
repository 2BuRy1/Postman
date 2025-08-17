import React, {useEffect, useState} from 'react';
import { Avatar, Space } from 'antd';
const ProfileAvatar = () => {

    const [avatar, setAvatar] = useState(null);


    useEffect(() => {
        fetch('http://localhost:8080/image', {method: 'GET', 'credentials': 'include'})
            .then(res => res.json()).then(data => setAvatar(data.image))


    }, [] )


    return   (

        <Space direction="vertical" size={16}>
            <Space wrap size={16}>
                <Avatar size="large" src={avatar} />
            </Space>

        </Space>
    );
}
export default ProfileAvatar;