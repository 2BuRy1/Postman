import {useEffect} from "react";

const Main = () => {

    useEffect(() => {

        fetch("http://localhost:8080/lal").then((response) => {
            console.log(response);
        })


    })



}


export default Main;