import ErrorIcon from "@/assets/error.svg";
import PasswordRequestSuccess from "@/assets/password-request-success.png";
import SuccessIcon from "@/assets/success.svg";
import AuthLoading from "@/components/auth-loading";
import AuthWrapper from "@/components/auth-wrapper";
import {
	Card,
	CardDescription,
	CardHeader,
	CardTitle,
} from "@/components/ui/card";
import { FieldDescription } from "@/components/ui/field";
import { useAuth } from "@/hooks/use-auth";
import {
	useMutation,
	useQuery,
	type UseQueryResult,
} from "@tanstack/react-query";
import { GalleryVerticalEnd, Loader2 } from "lucide-react";
import { Navigate, useSearchParams } from "react-router-dom";

export function VerifyEmail() {
	const [searchParams] = useSearchParams();
	const token = searchParams.get("token");

	const { user, isLoading, isAuthenticated, verifyEmail, logout } = useAuth();

	const verifyEmailQuery = useQuery({
		queryKey: ["verifyEmail", token],
		queryFn: async () => {
			return await verifyEmail({ token: token! });
		},
		gcTime: 0,
		enabled: !!token && isAuthenticated && !user?.emailVerified,
		retry: false,
		refetchOnWindowFocus: false,
	});

	const logoutMutation = useMutation({
		retry: false,
		mutationFn: logout,
	});

	if (isLoading) return <AuthLoading />;

	if (isAuthenticated === false) {
		return <Navigate to="/login" replace />;
	}

	if (user?.emailVerified === true) {
		return <Navigate to="/profile" replace />;
	}

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
				<div className="flex flex-col gap-4">
					<Card className="gap-4">
						{token === null ? (
							<EmailSent />
						) : (
							<EmailVerification
								verifyEmailQuery={verifyEmailQuery}
							/>
						)}
					</Card>
					<FieldDescription className="text-center">
						<a
							onClick={() => logoutMutation.mutate()}
							className="cursor-pointer"
						>
							Back to login
						</a>
					</FieldDescription>
				</div>
			</div>
		</AuthWrapper>
	);
}

function EmailVerification({
	verifyEmailQuery,
}: {
	verifyEmailQuery: UseQueryResult<string | null, Error>;
}) {
	if (verifyEmailQuery.isFetching)
		return (
			<CardHeader className="text-center gap-3">
				<Loader2 className="animate-spin m-auto stroke-muted-foreground" />
				<CardDescription>
					We&apos;re verifying your email, give us a second.
				</CardDescription>
			</CardHeader>
		);

	if (verifyEmailQuery.isSuccess)
		return (
			<CardHeader className="text-center gap-3">
				<img
					src={SuccessIcon}
					alt="Email Verification Success"
					className="size-10 mx-auto"
				/>
				<CardTitle className="text-xl mt-1">
					Your account is now verified!
				</CardTitle>
				<CardDescription>{verifyEmailQuery.data}</CardDescription>
			</CardHeader>
		);

	if (verifyEmailQuery.isError && verifyEmailQuery.error) {
		return (
			<CardHeader className="text-center gap-3">
				<img
					src={ErrorIcon}
					alt="Email Verification Error"
					className="size-9 mx-auto"
				/>
				<CardTitle className="text-xl mt-1">
					We couldn&apos;t verify your email!
				</CardTitle>
				<CardDescription>
					{verifyEmailQuery.error.message}
				</CardDescription>
				<ResendEmailField text="Let's try again!" />
			</CardHeader>
		);
	}

	return (
		<CardHeader className="text-center gap-3">
			<CardTitle className="text-xl mt-1">What are you up to?</CardTitle>
			<CardDescription>
				You shouldn&apos;t really be here! Your email is already
				verified!
			</CardDescription>
		</CardHeader>
	);
}

function EmailSent() {
	return (
		<CardHeader className="text-center gap-3">
			<img
				src={PasswordRequestSuccess}
				alt="Password Request Success"
				className="size-14 mx-auto"
			/>
			<CardTitle className="text-xl mt-1">Check your email</CardTitle>
			<CardDescription>
				We have sent instructions to your email so you can verify your
				account. Dont forget to check your junk emails.
			</CardDescription>
			<ResendEmailField />
		</CardHeader>
	);
}

function ResendEmailField({
	text = "Didn't receive the email?",
}: {
	text?: string;
}) {
	const { resendVerificationEmail } = useAuth();

	const resendVerificationEmailMutation = useMutation({
		retry: false,
		mutationFn: resendVerificationEmail,
	});

	return (
		<FieldDescription className="text-center">
			{resendVerificationEmailMutation.isError ? (
				<>
					<span>{resendVerificationEmailMutation.error.message}</span>
					<button
						className="hover:text-black underline cursor-pointer"
						onClick={() => resendVerificationEmailMutation.mutate()}
					>
						Try again
					</button>
				</>
			) : resendVerificationEmailMutation.isPending ? (
				<span>Sending email...</span>
			) : (
				<>
					<span>
						{text}{" "}
						<button
							className="hover:text-black underline cursor-pointer"
							onClick={() =>
								resendVerificationEmailMutation.mutate()
							}
						>
							Resend
						</button>
					</span>
				</>
			)}
		</FieldDescription>
	);
}
