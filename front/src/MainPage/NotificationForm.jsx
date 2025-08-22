import React, { useState } from "react";
import Papa from "papaparse";
import "../styles/NotificationForm.css";

export default function NotificationForm() {
    const [names, setNames] = useState([""]);
    const [message, setMessage] = useState("");


    const handleFileUpload = (event) => {
        const file = event.target.files?.[0];
        if (!file) return;

        Papa.parse(file, {
            complete: (results) => {
                const parsedNames = results.data
                    .flat()
                    .map((name) => String(name).trim())
                    .filter((name) => name.length > 0);

                setNames((prev) => [...prev, ...parsedNames]);
            },
        });
    };

    const handleAddName = () => setNames([...names, ""]);

    const handleNameChange = (index, value) => {
        const newNames = [...names];
        newNames[index] = value;
        setNames(newNames);
    };

    const handleSubmit = () => {

        fetch(`http://localhost:8081/notificate`, {method: "POST",
            credentials: "include",
                body: {"names" : names, "message": message}})
            .then(res => res.json())

    };

    return (
        <div className="notification-form">
            <h2>Web-Push рассылка</h2>

            <div className="form-grid">
                <div className="names-column">
                    <label>Имена получателей</label>
                    {names.map((name, index) => (
                        <input
                            key={index}
                            type="text"
                            placeholder="Введите имя"
                            value={name}
                            onChange={(e) => handleNameChange(index, e.target.value)}
                        />
                    ))}
                    <button type="button" onClick={handleAddName}>
                        + Добавить имя
                    </button>
                </div>

                <div className="csv-column">
                    <label>Загрузить CSV с именами</label>
                    <input type="file" accept=".csv" onChange={handleFileUpload} />
                </div>
            </div>

            <div className="message-box">
                <label>Текст уведомления</label>
                <input
                    type="text"
                    placeholder="Введите сообщение..."
                    value={message}
                    onChange={(e) => setMessage(e.target.value)}
                />
            </div>

            <button onClick={handleSubmit} className="send-btn">
                Отправить уведомление
            </button>
        </div>
    );
}