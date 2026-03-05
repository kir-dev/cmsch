import { useConfigContext } from '@/api/contexts/config/ConfigContext'
import { useBrandColor } from '@/util/core-functions.util.ts'
import { Rocket, Stamp } from 'lucide-react'
import type { FC } from 'react'

interface StampComponentProps {
  title?: string
  type: string
}

export const StampComponent: FC<StampComponentProps> = ({ title, type }: StampComponentProps) => {
  const component = useConfigContext()?.components?.token
  const brandColor = useBrandColor()

  const Icon = type === component?.collectRequiredType ? Stamp : Rocket

  return (
    <div className="max-w-md min-w-full md:min-w-[28rem] rounded-lg bg-secondary text-secondary-foreground flex border">
      <div className="bg-foreground/5 p-2 rounded-l-lg flex items-center justify-center border-r">
        <Icon className="h-8 w-8" style={{ color: brandColor }} />
      </div>
      <div className="w-full pl-3 flex items-center justify-center text-center">
        <span className="text-xl font-bold">{title}</span>
      </div>
    </div>
  )
}
