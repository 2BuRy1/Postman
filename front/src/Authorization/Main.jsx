import {useEffect} from "react";

const Main = () => {

    useEffect(() => {


        const data = {
            headers: {
                'Content-Type': 'application/json'
            },
            'method' : 'GET',
            'credentials': 'include',
        }



        fetch("http://localhost:8080/auth_check", data).then((response) => {
            console.log(response);
        })


    })



}


export default Main;