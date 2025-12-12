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
import { Input } from "@/components/ui/input";
import type { dtoPasswordRequestReset } from "@/lib/types";
import { AlertCircle, GalleryVerticalEnd, Loader2 } from "lucide-react";
import { useState } from "react";
import { Link } from "react-router-dom";

export function PasswordRequestReset() {
	const { passwordRequestReset } = useAuth();

	const [form, setForm] = useState<dtoPasswordRequestReset>({
		email: "",
	});

	const handleChange =
		(field: keyof dtoPasswordRequestReset) =>
		(event: React.ChangeEvent<HTMLInputElement>) => {
			setForm((prev) => ({
				...prev,
				[field]: event.target.value,
			}));
		};

	return (
		<div className="bg-muted flex min-h-svh flex-col items-center justify-center gap-6 p-6 md:p-10">
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
				<div className={"flex flex-col gap-6"}>
					<Card className="gap-4">
						<CardHeader className="text-center">
							<CardTitle className="text-xl">
								Reset password
							</CardTitle>
							<CardDescription>
								No worries, we&apos;ll send you reset
								instructions.
							</CardDescription>
						</CardHeader>
						<CardContent>
							<FieldGroup className="gap-6">
								<Field>
									<FieldLabel htmlFor="email">
										Email address
									</FieldLabel>
									<Input
										id="email"
										type="email"
										placeholder="email@example.com"
										required
										value={form.email}
										onChange={handleChange("email")}
									/>
								</Field>
								{passwordRequestReset.error && (
									<div className="rounded-sm border border-red-500 bg-red-100/80 px-3 py-3 flex flex-row items-center gap-2">
										<AlertCircle className="size-5 stroke-red-500 stroke-2" />
										<p className="text-sm text-red-500">
											{passwordRequestReset.error.message}
										</p>
									</div>
								)}
								<Field className="flex flex-col gap-3">
									<Button
										type="button"
										onClick={() =>
											passwordRequestReset.mutate(form)
										}
										disabled={
											passwordRequestReset.isPending
										}
									>
										{passwordRequestReset.isPending ? (
											<Loader2 className="size-4 animate-spin" />
										) : null}
										Send
									</Button>
									<FieldDescription className="text-center">
										<Link to={"/login"}>Back to login</Link>
									</FieldDescription>
								</Field>
							</FieldGroup>
						</CardContent>
					</Card>
				</div>
			</div>
		</div>
	);
}
