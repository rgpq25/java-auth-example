import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import { createBrowserRouter, RouterProvider } from "react-router-dom";
import App from "./App.tsx";
import AuthProvider from "./components/auth-provider.tsx";
import ProtectedRoute from "./components/protected-route.tsx";
import "./index.css";
import { Login, Profile, Register, VerifyEmail } from "./pages";

const router = createBrowserRouter([
	{
		path: "/",
		element: (
			<AuthProvider>
				<App />
			</AuthProvider>
		),
		children: [
			{ path: "register", element: <Register /> },
			{ path: "login", element: <Login /> },
			{ path: "verify-email", element: <VerifyEmail /> },
			{
				path: "profile",
				element: (
					<ProtectedRoute>
						<Profile />
					</ProtectedRoute>
				),
			},
		],
	},
]);

const queryClient = new QueryClient();

createRoot(document.getElementById("root")!).render(
	<StrictMode>
		<QueryClientProvider client={queryClient}>
			<RouterProvider router={router} />
		</QueryClientProvider>
	</StrictMode>
);
