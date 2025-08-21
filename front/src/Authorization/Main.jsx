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
            .then(response => {
                if(response.status === 401) {
                    fetch("http://localhost:8080/auth", {  'method' : 'GET',
                        'credentials': 'include',})
            }
        })
            }
        , [])


    const on = () => {
        fetch("http://localhost:8081/test_not", {method: 'GET', credentials: 'include'})
            .then(res => console.log(res))
    }

    return (
        <>
        <div id="header">
           <ProfileAvatar/>
        </div>
        <button onClick={on}/>
        </>
    )



}


export default Main;