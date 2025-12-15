import { api } from "@/api/api-client";
import { Button } from "@/components/ui/button";
import { useAuth } from "@/hooks/use-auth";
import { useMutation } from "@tanstack/react-query";

export function Profile() {
	const { user, logout } = useAuth();

	const logoutMutation = useMutation({
		retry: false,
		mutationFn: logout,
	});

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
				<Button onClick={() => logoutMutation.mutate()}>Log out</Button>
			</div>
		</div>
	);
}
