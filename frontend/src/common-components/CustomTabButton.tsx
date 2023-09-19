import { Tab, TabProps } from '@chakra-ui/react'

export function CustomTabButton({ color, ...props }: TabProps) {
  return <Tab color={color ?? 'chakra-body-text'} {...props} />
}
