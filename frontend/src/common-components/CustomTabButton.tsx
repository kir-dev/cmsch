export function CustomTabButton({ color, ...props }: TabProps) {
  return <Tab color={color ?? 'chakra-body-text'} {...props} />
}
