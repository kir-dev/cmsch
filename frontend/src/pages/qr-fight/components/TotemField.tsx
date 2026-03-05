import { useOpaqueBackground } from '@/util/core-functions.util'
import type { Totem } from '@/util/views/qrFight.view'

interface TotemFieldProps {
  totem: Totem
}

export function TotemField({ totem }: TotemFieldProps) {
  const background = useOpaqueBackground(3)

  return (
    <div className="flex-1 basis-40 p-5 rounded-md" style={{ backgroundColor: background }}>
      <div className="text-2xl font-bold">{totem.name}</div>
      {totem.description && <div className="text-sm mt-1">{totem.description}</div>}
      <div className="flex flex-col items-start mt-3 space-y-0">
        <p className="text-xs text-muted-foreground uppercase">Befoglalta</p>
        <div className="font-bold">{totem.owner || 'Még senki'}</div>
      </div>
    </div>
  )
}
