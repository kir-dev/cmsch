import { cn } from '@/lib/utils'
import { useNavigate } from 'react-router'

interface BoardStatProps extends React.HTMLAttributes<HTMLDivElement> {
  label: string
  value: string | number
  subValue?: string | number
  navigateTo?: string
}

export const BoardStat = ({ label, value, subValue, navigateTo, className, ...props }: BoardStatProps) => {
  const navigate = useNavigate()
  return (
    <div
      className={cn(
        'rounded-lg bg-secondary text-secondary-foreground p-5 transition-colors border',
        navigateTo && 'cursor-pointer hover:bg-secondary/80',
        className
      )}
      onClick={() => {
        if (navigateTo) navigate(navigateTo)
      }}
      {...props}
    >
      <div className="text-sm font-medium text-muted-foreground">{label}</div>
      <div className="text-2xl font-bold">{value}</div>
      {subValue && <div className="text-sm text-muted-foreground">{subValue}</div>}
    </div>
  )
}
