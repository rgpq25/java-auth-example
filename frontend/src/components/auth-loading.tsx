import { Loader2 } from "lucide-react";

function AuthLoading() {
	return (
		<div className="bg-muted flex min-h-svh flex-col items-center justify-center">
			<Loader2 className="size-6 animate-spin stroke-muted-foreground" />
		</div>
	);
}
export default AuthLoading;
