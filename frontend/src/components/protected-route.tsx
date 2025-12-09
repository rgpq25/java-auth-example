import { type PropsWithChildren } from "react";
import { Navigate } from "react-router-dom";
import { useAuth } from "./auth-provider";

type ProtectedRouteProps = PropsWithChildren;

function ProtectedRoute({ children }: ProtectedRouteProps) {
	const { user, isLoading, isAuthenticated } = useAuth();

	if (isLoading) return null;

	if (isAuthenticated === false) {
		return <Navigate to="/login" replace />;
	}

	if (user?.emailVerified === false) {
		return <Navigate to="/verify-email" replace />;
	}

	return children;
}
export default ProtectedRoute;
