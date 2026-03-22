import { useStyle } from '@/api/contexts/config/ConfigContext.tsx'
import { Card, CardContent, CardHeader } from '@/components/ui/card'
import { cn } from '@/lib/utils'
import { useColorModeValue } from '@/util/core-functions.util.ts'

export interface CmschContainerProps extends React.HTMLAttributes<HTMLDivElement> {
  title?: string
  disablePadding?: boolean
}

export const CmschContainer = ({ children, title, disablePadding, className, ...props }: CmschContainerProps) => {
  const theme = useStyle()
  const containerFilter = useColorModeValue(theme?.lightContainerFilter, theme?.darkContainerFilter)

  return (
    <Card
      className={cn(
        'mx-auto w-full max-w-full flex-col border-none bg-card text-card-foreground shadow-xs sm:max-w-5xl md:rounded-xl',
        className
      )}
      style={{ backdropFilter: containerFilter }}
      {...props}
    >
      {title && (
        <CardHeader>
          <h2 className="text-2xl font-bold leading-none tracking-tight">{title}</h2>
        </CardHeader>
      )}
      <CardContent className={cn(disablePadding ? 'p-0' : 'p-6')}>{children}</CardContent>
    </Card>
  )
}
