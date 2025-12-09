import { useState } from "react";
import { useAuth } from "@/components/auth-provider";
import { Button } from "@/components/ui/button";
import {
	Field,
	FieldDescription,
	FieldGroup,
	FieldLabel,
} from "@/components/ui/field";
import {
	InputOTP,
	InputOTPGroup,
	InputOTPSlot,
} from "@/components/ui/input-otp";
import { cn } from "@/lib/utils";
import { useMutation } from "@tanstack/react-query";
import { AlertCircle } from "lucide-react";
import { Navigate } from "react-router-dom";

export function VerifyEmail() {
	const {
		user,
		isLoading,
		isAuthenticated,
		verifyEmail,
		resendVerificationEmail,
	} = useAuth();

	const [otp, setOtp] = useState("");

	const verifyEmailMutation = useMutation({
		mutationFn: (code: string) => verifyEmail(code),
	});

	const resendVerifEmail = useMutation({
		mutationFn: resendVerificationEmail,
	});

	if (isLoading) return null;

	if (isAuthenticated === false) {
		return <Navigate to="/login" replace />;
	}

	if (user?.emailVerified === true) {
		return <Navigate to="/profile" replace />;
	}

	const handleVerify = () => {
		if (otp.length !== 6) {
			return;
		}

		verifyEmailMutation.mutate(otp);
	};

	const handleResend = () => {
		resendVerifEmail.mutate();
	};

	return (
		<div className="flex h-dvh">
			<div className="w-1/2 h-full bg-secondary"></div>
			<div className="w-1/2 h-full flex">
				<div className={cn("flex flex-col gap-6", "w-full m-auto")}>
					<FieldGroup className="flex flex-col">
						<div className="flex flex-col items-center gap-1 text-center">
							<h1 className="text-2xl font-semibold">
								Enter verification code
							</h1>
							<p className="text-muted-foreground text-sm text-balance">
								We sent a 6-digit code to your email.
							</p>
						</div>

						<div className="mx-auto w-auto flex flex-col gap-5">
							<Field className="w-fit">
								<FieldLabel htmlFor="otp" className="sr-only">
									Verification code
								</FieldLabel>

								<InputOTP
									id="otp"
									maxLength={6}
									required
									className="min-w-fit"
									value={otp}
									onChange={setOtp} // <- controlled by React
								>
									<InputOTPGroup className="gap-2 *:data-[slot=input-otp-slot]:rounded-md *:data-[slot=input-otp-slot]:border min-w-fit">
										<InputOTPSlot index={0} />
										<InputOTPSlot index={1} />
										<InputOTPSlot index={2} />
										<InputOTPSlot index={3} />
										<InputOTPSlot index={4} />
										<InputOTPSlot index={5} />
									</InputOTPGroup>
								</InputOTP>

								<FieldDescription className="text-center">
									Enter the 6-digit code sent to your email.
								</FieldDescription>

								{verifyEmailMutation.isError && (
									<div className="rounded-sm border border-red-500 bg-red-100/80 px-3 py-3 flex flex-row items-center gap-2">
										<AlertCircle className="size-5 stroke-red-500 stroke-2" />
										<p className="text-sm text-red-500">
											{verifyEmailMutation.error.message}
										</p>
									</div>
								)}
							</Field>

							<FieldGroup className="flex flex-col gap-2">
								<Button
									type="button"
									className="w-full"
									onClick={handleVerify}
									disabled={
										otp.length !== 6 ||
										verifyEmailMutation.isPending
									}
								>
									{verifyEmailMutation.isPending
										? "Verifying..."
										: "Verify"}
								</Button>

								<FieldDescription className="text-center flex flex-col gap-1">
									{resendVerifEmail.isError ? (
										<>
											<span>
												{resendVerifEmail.error.message}
											</span>
											<Button
												type="button"
												variant="outline"
												size="sm"
												onClick={handleResend}
												disabled={
													resendVerifEmail.isPending
												}
											>
												Try again
											</Button>
										</>
									) : resendVerifEmail.isPending ? (
										<p>Sending email...</p>
									) : (
										<>
											<span>
												Didn&apos;t receive the code?{" "}
												<button
													className="hover:text-black underline cursor-pointer"
													onClick={handleResend}
												>
													Resend
												</button>
											</span>
										</>
									)}
								</FieldDescription>
							</FieldGroup>
						</div>
					</FieldGroup>
				</div>
			</div>
		</div>
	);
}
