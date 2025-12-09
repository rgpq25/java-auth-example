import { api } from "@/api/api-client";
import { useAuth } from "@/components/auth-provider";
import { Button } from "@/components/ui/button";

export function Profile() {
	const { user, logout } = useAuth();

	async function onRefetch() {
		try {
			const response = await api.get("/users/me");
			console.log(response.data);
		} catch (error) {
			console.log("Error while fetching profile", error);
		}
	}

	return (
		<div className="flex h-screen w-full">
			<div className="m-auto flex flex-col justify-center items-center gap-4">
				<p className="text-center">This is the profile page</p>
				<div>{JSON.stringify(user)}</div>
				<Button onClick={onRefetch}>Refetch</Button>
				<Button onClick={logout}>Log out</Button>
			</div>
		</div>
	);
}
