import { useAuth } from "@/components/auth-provider";
import { Button } from "@/components/ui/button";
import {
	Card,
	CardContent,
	CardDescription,
	CardHeader,
	CardTitle,
} from "@/components/ui/card";
import {
	Field,
	FieldDescription,
	FieldGroup,
	FieldLabel,
} from "@/components/ui/field";
import { Input } from "@/components/ui/input";
import { AlertCircle, GalleryVerticalEnd, Loader2 } from "lucide-react";
import { useState } from "react";
import { Link, useSearchParams } from "react-router-dom";

type PasswordResetForm = {
	email: string;
	password: string;
	confirmPassword: string;
};

export function PasswordReset() {
	const [searchParams] = useSearchParams();
	const token = searchParams.get("token");

	const { passwordReset } = useAuth();

	const [form, setForm] = useState<PasswordResetForm>({
		email: "",
		password: "",
		confirmPassword: "",
	});

	const handleChange =
		(field: keyof PasswordResetForm) =>
		(event: React.ChangeEvent<HTMLInputElement>) => {
			setForm((prev) => ({
				...prev,
				[field]: event.target.value,
			}));
		};

	return (
		<div className="bg-muted flex min-h-svh flex-col items-center justify-center gap-6 p-6 md:p-10">
			<div className="flex w-full max-w-sm flex-col gap-6">
				<a
					href="#"
					className="flex items-center gap-2 self-center font-medium"
				>
					<div className="bg-primary text-primary-foreground flex size-6 items-center justify-center rounded-md">
						<GalleryVerticalEnd className="size-4" />
					</div>
					Java Auth Example
				</a>
				<div className={"flex flex-col gap-6"}>
					<Card className="gap-4">
						<CardHeader className="text-center">
							<CardTitle className="text-xl">
								Create new password
							</CardTitle>
							<CardDescription>
								Make sure your password is secure.
							</CardDescription>
						</CardHeader>
						<CardContent>
							<FieldGroup className="gap-6">
								<Field>
									<FieldLabel htmlFor="email">
										Email
									</FieldLabel>
									<Input
										id="email"
										type="email"
										placeholder="email@example.com"
										required
										value={form.email}
										onChange={handleChange("email")}
									/>
								</Field>
								<Field>
									<FieldLabel htmlFor="email">
										Password
									</FieldLabel>
									<Input
										id="password"
										type="password"
										placeholder="safePassword"
										required
										value={form.password}
										onChange={handleChange("password")}
									/>
								</Field>
								<Field>
									<FieldLabel htmlFor="email">
										Confirm password
									</FieldLabel>
									<Input
										id="confirmPassword"
										type="password"
										placeholder="safePassword"
										required
										value={form.confirmPassword}
										onChange={handleChange(
											"confirmPassword"
										)}
									/>
								</Field>
								{passwordReset.error && (
									<div className="rounded-sm border border-red-500 bg-red-100/80 px-3 py-3 flex flex-row items-center gap-2">
										<AlertCircle className="size-5 stroke-red-500 stroke-2" />
										<p className="text-sm text-red-500">
											{passwordReset.error.message}
										</p>
									</div>
								)}
								<Field className="flex flex-col gap-3">
									<Button
										type="button"
										onClick={() =>
											passwordReset.mutate({
												token: token || "",
												email: form.email,
												password: form.confirmPassword,
											})
										}
										disabled={passwordReset.isPending}
									>
										{passwordReset.isPending ? (
											<Loader2 className="size-4 animate-spin" />
										) : null}
										Send
									</Button>
									<FieldDescription className="text-center">
										<Link to={"/login"}>Back to login</Link>
									</FieldDescription>
								</Field>
							</FieldGroup>
						</CardContent>
					</Card>
				</div>
			</div>
		</div>
	);
}
