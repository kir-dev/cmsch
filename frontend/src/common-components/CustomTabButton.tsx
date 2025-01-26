import { Tabs, TabsTriggerProps } from '@chakra-ui/react'

export function CustomTabButton({ color, ...props }: TabsTriggerProps) {
  return <Tabs.Trigger color={color ?? 'chakra-body-text'} {...props} />
}
