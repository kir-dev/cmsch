import type { QrLevelDto } from '@/util/views/qrFight.view'
import { TowerField } from './TowerField'

interface TowerDataDisplay {
  level: QrLevelDto
}

export function TowerDataDisplay({ level }: TowerDataDisplay) {
  return (
    <div className="flex flex-col items-start w-full space-y-2">
      <h3 className="text-lg font-bold">Tornyok</h3>
      <div className="flex flex-wrap w-full gap-5">
        {level.towers.map((t) => (
          <TowerField tower={t} key={t.name} />
        ))}
      </div>
    </div>
  )
}
