import SuccessIcon from "@/assets/success.svg";
import AuthLoading from "@/components/auth-loading";
import AuthWrapper from "@/components/auth-wrapper";
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
import { useAuth } from "@/hooks/use-auth";
import { useMutation } from "@tanstack/react-query";
import { AlertCircle, GalleryVerticalEnd, Loader2 } from "lucide-react";
import { useState } from "react";
import { Link, Navigate, useSearchParams } from "react-router-dom";

type PasswordResetForm = {
	password: string;
	confirmPassword: string;
};

export function PasswordReset() {
	const [searchParams] = useSearchParams();
	const token = searchParams.get("token");

	const { isLoading, isAuthenticated, passwordReset } = useAuth();

	const [form, setForm] = useState<PasswordResetForm>({
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

	const passwordResetMutation = useMutation({
		retry: false,
		mutationFn: passwordReset,
	});

	if (isLoading) return <AuthLoading />;

	if (isAuthenticated) return <Navigate to="/profile" replace />;

	return (
		<AuthWrapper>
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
				<div className={"flex flex-col gap-4"}>
					{passwordResetMutation.isSuccess ? (
						<Card className="gap-4">
							<CardHeader className="text-center gap-3">
								<img
									src={SuccessIcon}
									alt="Password Reset Success"
									className="size-10 mx-auto"
								/>
								<CardTitle className="text-xl mt-1">
									Your password has been changed!
								</CardTitle>
								<CardDescription>
									{passwordResetMutation.data}
								</CardDescription>
							</CardHeader>
						</Card>
					) : (
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
									{passwordResetMutation.error && (
										<div className="rounded-sm border border-red-500 bg-red-100/80 px-3 py-3 flex flex-row items-center gap-2">
											<AlertCircle className="size-5 stroke-red-500 stroke-2" />
											<p className="text-sm text-red-500">
												{
													passwordResetMutation.error
														.message
												}
											</p>
										</div>
									)}
									<Field className="flex flex-col gap-3">
										<Button
											type="button"
											onClick={() =>
												passwordResetMutation.mutate({
													token: token || "",
													password:
														form.confirmPassword,
												})
											}
											disabled={
												passwordResetMutation.isPending
											}
										>
											{passwordResetMutation.isPending ? (
												<Loader2 className="size-4 animate-spin" />
											) : null}
											Send
										</Button>
									</Field>
								</FieldGroup>
							</CardContent>
						</Card>
					)}
					<FieldDescription className="text-center">
						<Link to={"/login"}>Back to login</Link>
					</FieldDescription>
				</div>
			</div>
		</AuthWrapper>
	);
}
