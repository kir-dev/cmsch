import { TabsTrigger } from '@/components/ui/tabs'

export function CustomTabButton({ children, value, ...props }: { children: React.ReactNode; value: string }) {
  return (
    <TabsTrigger value={value} {...props}>
      {children}
    </TabsTrigger>
  )
}
