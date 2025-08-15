import {useEffect} from "react";

const Main = () => {

    useEffect(() => {


        const data = {
            headers: {
                'Content-Type': 'application/json'
            },
            'method' : 'POST',
            'credentials': 'include',
        }



        fetch("http://localhost:8080/lal", data).then((response) => {
            console.log(response);
        })


    })



}


export default Main;