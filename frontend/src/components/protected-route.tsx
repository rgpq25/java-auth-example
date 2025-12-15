import { type PropsWithChildren } from "react";
import { Navigate } from "react-router-dom";
import AuthLoading from "./auth-loading";
import { useAuth } from "@/hooks/use-auth";

type ProtectedRouteProps = PropsWithChildren;

function ProtectedRoute({ children }: ProtectedRouteProps) {
	const { user, isLoading, isAuthenticated } = useAuth();

	if (isLoading) return <AuthLoading />;

	if (isAuthenticated === false) {
		return <Navigate to="/login" replace />;
	}

	if (user?.emailVerified === false) {
		return <Navigate to="/email/verify" replace />;
	}

	return children;
}
export default ProtectedRoute;
