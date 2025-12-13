import type { PropsWithChildren } from "react";

function AuthWrapper({ children }: PropsWithChildren) {
	return (
		<div className="bg-muted flex min-h-svh flex-row items-center justify-center">
			<div className="flex w-1/2 bg-muted-foreground min-h-svh" />
			<div className="flex w-1/2 items-center justify-center">
				{children}
			</div>
		</div>
	);
}
export default AuthWrapper;
