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
import {
	InputOTP,
	InputOTPGroup,
	InputOTPSlot,
} from "@/components/ui/input-otp";
import { useMutation } from "@tanstack/react-query";
import { AlertCircle, GalleryVerticalEnd, Loader2 } from "lucide-react";
import { useState } from "react";
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
		<div className="bg-muted flex min-h-svh flex-col items-center justify-center gap-6 p-6 md:p-10">
			<div className="flex w-full max-w-xs flex-col gap-6">
				<a
					href="#"
					className="flex items-center gap-2 self-center font-medium"
				>
					<div className="bg-primary text-primary-foreground flex size-6 items-center justify-center rounded-md">
						<GalleryVerticalEnd className="size-4" />
					</div>
					Java Auth Example
				</a>
				<Card>
					<CardHeader className="text-center">
						<CardTitle className="text-xl">
							Enter verification code
						</CardTitle>
						<CardDescription>
							We sent a 6-digit code to your email.
						</CardDescription>
					</CardHeader>
					<CardContent>
						<FieldGroup>
							<Field>
								<FieldLabel htmlFor="otp" className="sr-only">
									Verification code
								</FieldLabel>
								<InputOTP
									maxLength={6}
									id="otp"
									required
									value={otp}
									onChange={setOtp}
								>
									<InputOTPGroup className="gap-2.5 *:data-[slot=input-otp-slot]:rounded-md *:data-[slot=input-otp-slot]:border">
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
							</Field>
							{verifyEmailMutation.isError && (
								<div className="rounded-sm border border-red-500 bg-red-100/80 px-3 py-3 flex flex-row items-center gap-2">
									<AlertCircle className="size-5 stroke-red-500 stroke-2" />
									<p className="text-sm text-red-500">
										{verifyEmailMutation.error.message}
									</p>
								</div>
							)}
							<Field className="flex flex-col gap-3">
								<Button
									onClick={handleVerify}
									disabled={
										verifyEmailMutation.isPending ||
										otp.length < 6
									}
								>
									{verifyEmailMutation.isPending ? (
										<Loader2 className="size-4 animate-spin" />
									) : null}
									Verify
								</Button>
								<FieldDescription className="text-center">
									{resendVerifEmail.isError ? (
										<>
											<span>
												{resendVerifEmail.error.message}
											</span>
											<button
												className="hover:text-black underline cursor-pointer"
												onClick={handleResend}
											>
												Try again
											</button>
										</>
									) : resendVerifEmail.isPending ? (
										<span>Sending email...</span>
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
							</Field>
						</FieldGroup>
					</CardContent>
				</Card>
			</div>
		</div>
	);
}
