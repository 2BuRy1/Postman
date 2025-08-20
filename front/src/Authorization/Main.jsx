import {useEffect, useRef, useState} from "react";
import {useNavigate} from "react-router";
import ProfileAvatar from "../Profile/ProfileAvatar";
import '../styles/Main.css';

const Main = () => {


    const navigate = useNavigate()

    useEffect(() => {


        const data = {

            'method' : 'GET',
            'credentials': 'include',
        }



        fetch("http://localhost:8080/auth_check", data)
            .then(response => {if(response.ok) {
            }
        })
            .catch( error => {
            console.error("pizdec!!");
            navigate('/login')
            }
        )
    })




    return (
        <div id="header">
           <ProfileAvatar/>
        </div>
    )



}


export default Main;