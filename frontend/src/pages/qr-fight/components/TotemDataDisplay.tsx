import type { QrLevelDto } from '@/util/views/qrFight.view'
import { TotemField } from './TotemField'

interface TotemDataDisplay {
  level: QrLevelDto
}

export function TotemDataDisplay({ level }: TotemDataDisplay) {
  return (
    <div className="flex flex-col items-start w-full space-y-2">
      <h3 className="text-lg font-bold">Totemek</h3>
      <div className="flex flex-wrap w-full gap-5">
        {level.totems.map((t) => (
          <TotemField totem={t} key={t.name} />
        ))}
      </div>
    </div>
  )
}
