import GoogleIcon from "@/assets/google-icon.svg";
import { useAuth } from "@/components/auth-provider";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Separator } from "@/components/ui/separator";
import { Loader2 } from "lucide-react";
import { useState } from "react";
import { Link, Navigate, useNavigate } from "react-router-dom";

type RegisterForm = {
	name: string;
	email: string;
	password: string;
};

export function Register() {
	const { isAuthenticated, isLoading, register } = useAuth();
	const navigate = useNavigate();

	const [registerForm, setRegisterForm] = useState<RegisterForm>({
		name: "",
		email: "",
		password: "",
	});
	const [isFormLoading, setIsFormLoading] = useState(false);

	async function onRegister() {
		try {
			setIsFormLoading(true);

			await register({
				name: registerForm.name,
				email: registerForm.email,
				password: registerForm.password,
			});

			navigate("/profile", { replace: true });
		} finally {
			setIsFormLoading(false);
		}
	}

	async function onSignUpWithGoogle() {
		return;
	}

	if (isLoading) return null;

	if (isAuthenticated === true) return <Navigate to="/profile" replace />;

	return (
		<div className="flex h-dvh">
			<div className="w-1/2 h-full bg-secondary"></div>
			<div className="w-1/2 h-full flex">
				<div className="m-auto flex flex-col gap-4 w-xs">
					<header>
						<h1 className="text-2xl font-semibold text-center">
							Create an account
						</h1>
						<p className="text-muted-foreground text-center">
							Start your journey by registering your account
						</p>
					</header>
					<section className="space-y-2">
						<InputField
							field="name"
							placeholder="Jhon"
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
						disabled={isFormLoading}
					>
						{isFormLoading ? (
							<Loader2 className="size-4 animate-spin" />
						) : null}
						Register
					</Button>
					<section className="flex items-center gap-2">
						<Separator
							orientation="horizontal"
							className="flex-1"
						/>
						<span className="shrink-0 text-muted-foreground leading-3">
							or
						</span>
						<Separator
							orientation="horizontal"
							className="flex-1"
						/>
					</section>
					<Button
						variant="outline"
						className="items-center flex"
						onClick={onSignUpWithGoogle}
					>
						<img
							src={GoogleIcon}
							alt="Google Icon"
							className="size-4"
						/>
						Sign up with google
					</Button>
					<section>
						<p className="text-sm text-center text-muted-foreground">
							Already have an account?{" "}
							<Link
								className="hover:underline font-medium text-primary cursor-pointer"
								to={"/login"}
							>
								Sign in
							</Link>
						</p>
					</section>
				</div>
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
