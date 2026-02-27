import { Separator } from '@/components/ui/separator'
import { useOpaqueBackground } from '@/util/core-functions.util'
import type { Tower } from '@/util/views/qrFight.view'

interface TowerFieldProps {
  tower: Tower
}

export function TowerField({ tower }: TowerFieldProps) {
  const background = useOpaqueBackground(3)

  return (
    <div className="flex-1 basis-40 p-5 rounded-md" style={{ backgroundColor: background }}>
      <div className="text-2xl font-bold">{tower.name}</div>
      {tower.description && <div className="text-sm mt-1">{tower.description}</div>}
      <div className="flex flex-col items-start mt-3 space-y-3">
        <div className="flex flex-col items-start space-y-0">
          <p className="text-xs text-muted-foreground uppercase">Aktuális foglaló</p>
          <div className="font-bold">{tower.ownerNow || 'Nincs'}</div>
        </div>
        <Separator />
        <div className="flex flex-col items-start space-y-0">
          <p className="text-xs text-muted-foreground uppercase">Helytartó</p>
          <div className="font-bold">{tower.holder || 'Nincs'}</div>
          {tower.holdingFor !== undefined && tower.holder && <div className="text-sm">~{tower.holdingFor} perce</div>}
        </div>
      </div>
    </div>
  )
}
