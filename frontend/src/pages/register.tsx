import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import axios from "axios";
import { Loader2 } from "lucide-react";
import { useState } from "react";

type RegisterForm = {
	name: string;
	lastname: string;
	email: string;
	password: string;
};

export function Register() {
	const [registerForm, setRegisterForm] = useState<RegisterForm>({
		name: "",
		lastname: "",
		email: "",
		password: "",
	});
	const [isLoading, setIsLoading] = useState(false);

	async function onRegister() {
		try {
			setIsLoading(true);
			const response = await axios.post(
				"http://localhost:8080/auth/register",
				{
					name: registerForm.name,
					lastname: registerForm.lastname,
					email: registerForm.email,
					password: registerForm.password,
				}
			);

			const accessToken = response.data.accessToken;

			// save this in a context and set up a hook to get it quickly through any page. (accesor should return to /login if not authenticated)

			console.log(response);
		} catch (error) {
			console.error(error);
		} finally {
			setIsLoading(false);
		}
	}

	return (
		<div className="flex h-dvh">
			<div className="m-auto flex flex-col gap-4 w-xs">
				<h1 className="text-2xl font-semibold">Register</h1>
				<section className="space-y-2">
					<InputField
						field="name"
						placeholder="Jhon"
						value={registerForm}
						onChange={setRegisterForm}
					/>
					<InputField
						field="lastname"
						placeholder="Doe"
						value={registerForm}
						onChange={setRegisterForm}
					/>
					<InputField
						field="email"
						placeholder="youremail@hotmail.com"
						value={registerForm}
						onChange={setRegisterForm}
					/>
					<InputField
						field="password"
						placeholder="verySecurePassword"
						value={registerForm}
						onChange={setRegisterForm}
					/>
				</section>
				<Button
					className="flex w-full items-center justify-center"
					onClick={onRegister}
					disabled={isLoading}
				>
					{isLoading ? (
						<Loader2 className="size-4 animate-spin" />
					) : null}
					Register
				</Button>
			</div>
		</div>
	);
}

function InputField({
	field,
	placeholder,
	value,
	onChange,
}: {
	field: keyof RegisterForm;
	placeholder: string;
	value: RegisterForm;
	onChange: (val: RegisterForm) => void;
}) {
	return (
		<div className="flex flex-col gap-1">
			<p className="text-sm font-semibold capitalize">{field}</p>
			<Input
				placeholder={placeholder}
				value={value[field]}
				onChange={(e) =>
					onChange({
						...value,
						[field]: e.target.value,
					})
				}
			/>
		</div>
	);
}
