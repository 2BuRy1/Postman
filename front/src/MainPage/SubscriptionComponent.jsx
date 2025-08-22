import { useEffect, useState } from "react";
import { useParams } from "react-router";
import CustomButton from "./CustomButton";
import '../styles/Subscription.css'
const SubscriptionComponent = () => {
    const [denied, setDenied] = useState(Notification.permission === "denied");
    const [subscribed, setSubscribed] = useState(false);
    const [publicKey, setPublicKey] = useState("");
    const { id } = useParams();
    const [name, setName] = useState("");

    useEffect(() => {
        navigator.serviceWorker.getRegistration().then((registration) => {
            registration?.pushManager.getSubscription().then((sub) => {
                setSubscribed(!!sub);
            });
        });

        fetch("http://localhost:8081/get_key", {
            method: "GET",
            credentials: "include",
        })
            .then((res) => res.json())
            .then((data) => setPublicKey(data.key))
            .catch(() => {
                fetch("http://localhost:8080/auth", { method: "GET", credentials: "include" });
            });
    }, []);

    const subscribe = async () => {
        const permission = await Notification.requestPermission();
        if (permission !== "granted") {
            setDenied(true);
            return;
        }

        if (!publicKey) {
            alert("Public key not loaded yet");
            return;
        }

        const registration = await navigator.serviceWorker.register("/service-worker.js");

        const subscription = await registration.pushManager.subscribe({
            userVisibleOnly: true,
            applicationServerKey: urlBase64ToUint8Array(publicKey),
        });

        if (name !== "" && name) {
            await fetch(`http://localhost:8081/save-subscription/${id}?name=${name}`, {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify(subscription),
                credentials: "include",
            });
        }

        setSubscribed(true);
        alert("Subscribed successfully!");
    };

    const unsubscribe = async () => {
        const registration = await navigator.serviceWorker.getRegistration();
        const subscription = await registration?.pushManager.getSubscription();
        if (subscription) {
            await subscription.unsubscribe();
                await fetch(`http://localhost:8081/unsubscribe/${id}?name=${name}`, {
                    method: "POST",
                    headers: {"Content-Type": "application/json"},
                    body: JSON.stringify({endpoint: subscription.endpoint}),
                    credentials: "include",
                });

                setSubscribed(false);
                alert("Unsubscribed successfully!");
            }

    };

    function urlBase64ToUint8Array(base64String) {
        const padding = "=".repeat((4 - (base64String.length % 4)) % 4);
        const base64 = (base64String + padding).replace(/-/g, "+").replace(/_/g, "/");
        const raw = atob(base64);
        return Uint8Array.from([...raw].map((c) => c.charCodeAt(0)));
    }

    const onInputName = (e) => {
        console.log(e)
        setName(e.target.value);
    }

    return (
        <div className="subscriptionComponent">
            <h1>Web Push Notifications 📣</h1>
            {denied && (
                <b>
                    You have blocked notifications. You need to manually enable them in your browser.
                </b>
            )}

            {subscribed ? (
                <div>
                    <CustomButton onClick={unsubscribe} text="Unsubscribe" className={"subscribeButton"} />
                </div>
            ) : (
                <div className={"subscriptionContainer"}>
                    <input placeholder={"Type your name"} className={"nameInput"} onInput={onInputName}></input>

                    <CustomButton onClick={subscribe} text="Subscribe" className={"subscribeButton"}/>
                </div>
            )}
        </div>
    );
};

export default SubscriptionComponent;