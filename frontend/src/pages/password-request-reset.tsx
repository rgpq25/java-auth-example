import PasswordRequestSuccess from "@/assets/password-request-success.png";
import AuthLoading from "@/components/auth-loading";
import { useAuth } from "@/components/auth-provider";
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
import type { dtoPasswordRequestReset } from "@/lib/types";
import { useMutation } from "@tanstack/react-query";
import { AlertCircle, GalleryVerticalEnd, Loader2 } from "lucide-react";
import { useState } from "react";
import { Link, Navigate } from "react-router-dom";

export function PasswordRequestReset() {
	const { isLoading, isAuthenticated, passwordRequestReset } = useAuth();

	const [form, setForm] = useState<dtoPasswordRequestReset>({
		email: "",
	});

	const handleChange =
		(field: keyof dtoPasswordRequestReset) =>
		(event: React.ChangeEvent<HTMLInputElement>) => {
			setForm((prev) => ({
				...prev,
				[field]: event.target.value,
			}));
		};

	const passwordRequestResetMutation = useMutation({
		retry: false,
		mutationFn: passwordRequestReset,
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
					{passwordRequestResetMutation.isSuccess ? (
						<>
							<Card className="gap-4">
								<CardHeader className="text-center gap-2">
									<img
										src={PasswordRequestSuccess}
										alt="Password Request Success"
										className="size-14 mx-auto"
									/>
									<CardTitle className="text-xl mt-1">
										Check your email
									</CardTitle>
									<CardDescription>
										We have sent password recover
										instructions to your email. Dont forget
										to check your junk emails.
									</CardDescription>
								</CardHeader>
							</Card>
							<FieldDescription className="text-center">
								<Link to={"/login"}>Back to login</Link>
							</FieldDescription>
						</>
					) : (
						<>
							<Card className="gap-4">
								<CardHeader className="text-center">
									<CardTitle className="text-xl">
										Reset password
									</CardTitle>
									<CardDescription>
										No worries, we&apos;ll send you reset
										instructions.
									</CardDescription>
								</CardHeader>
								<CardContent>
									<FieldGroup className="gap-6">
										<Field>
											<FieldLabel htmlFor="email">
												Email address
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
										{passwordRequestResetMutation.error && (
											<div className="rounded-sm border border-red-500 bg-red-100/80 px-3 py-3 flex flex-row items-center gap-2">
												<AlertCircle className="size-5 stroke-red-500 stroke-2" />
												<p className="text-sm text-red-500">
													{
														passwordRequestResetMutation
															.error.message
													}
												</p>
											</div>
										)}
										<Field className="flex flex-col gap-3">
											<Button
												type="button"
												onClick={() =>
													passwordRequestResetMutation.mutate(
														form
													)
												}
												disabled={
													passwordRequestResetMutation.isPending
												}
											>
												{passwordRequestResetMutation.isPending ? (
													<Loader2 className="size-4 animate-spin" />
												) : null}
												Send
											</Button>
										</Field>
									</FieldGroup>
								</CardContent>
							</Card>
							<FieldDescription className="text-center">
								<Link to={"/login"}>Back to login</Link>
							</FieldDescription>
						</>
					)}
				</div>
			</div>
		</AuthWrapper>
	);
}
