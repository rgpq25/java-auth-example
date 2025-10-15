import axios from "axios";
import { useEffect, useState } from "react";

function App() {
	const [message, setMessage] = useState("waiting...");

	useEffect(() => {
		async function initialRequest() {
			try {
				const response = await axios.get(
					"http://localhost:8080/example"
				);
				setMessage(response.data);
			} catch (error) {
				console.error("There was an error:", error);
				setMessage("Error fetching message!");
			}
		}

		initialRequest();
	}, []);

	return (
		<div className="w-dvw h-dvh flex flex-col items-center justify-center">
			<h1 className="text-3xl">Java Auth Example</h1>
			<p>Initial response message: {message}</p>
		</div>
	);
}

export default App;
