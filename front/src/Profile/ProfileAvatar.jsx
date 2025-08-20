import React, {useEffect, useState} from 'react';
import { Avatar, Space } from 'antd';
const ProfileAvatar = () => {

    const [avatar, setAvatar] = useState(null);


    useEffect(() => {
        fetch('http://localhost:8080/image', {method: 'GET', 'credentials': 'include'})
            .then(res => res.json()).then(data => setAvatar(data.image))


    }, [] )


    return   (
                <Avatar size={64} src={avatar} />

    );
}
export default ProfileAvatar;