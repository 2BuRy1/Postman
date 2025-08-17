import {useEffect, useRef, useState} from "react";
import {useNavigate} from "react-router";
import ProfileAvatar from "../Profile/ProfileAvatar";

const Main = () => {


    const navigate = useNavigate()

    const [buttonValue, setButtonValue] = useState(0);


    function handleClick() {


        const data = {

            'method' : 'GET',
            'credentials': 'include'
        }


        fetch(`http://localhost:8080/button_pizdec?button=${buttonValue}`, data).then((response) => response.json())
            .then(data => setButtonValue(data.value))




        }

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
        <div>
            <button onClick = {handleClick} ></button>
           {buttonValue}
            <ProfileAvatar/>
        </div>
    )



}


export default Main;