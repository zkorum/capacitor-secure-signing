export interface SecureSigningPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
