import { cn } from '@/lib/utils'
import { getTextColorFromLuminance } from '@/util/color.utils'
import { MapMarkerShape } from '@/util/views/map.view'
import { FaBroadcastTower, FaCampground, FaCar, FaCrosshairs, FaHome, FaInfo, FaMarker, FaUser } from 'react-icons/fa'

interface MapMarkerProps {
  color: string
  text?: string
  markerShape?: MapMarkerShape
}

export function MapMarker({ color, text, markerShape = MapMarkerShape.CIRCLE }: MapMarkerProps) {
  return (
    <div className="flex flex-col items-center w-[200px] space-y-1">
      <div
        className={cn(
          'h-6 w-6 border-2 border-white flex items-center justify-center box-border',
          markerShape === MapMarkerShape.SQUARE ? 'rounded-md' : 'rounded-full'
        )}
        style={{ backgroundColor: color }}
      >
        <MarkerShapeIcon markerShape={markerShape} color={getTextColorFromLuminance(color)} size={12} />
      </div>
      {text && (
        <div className="py-0.5 px-2 rounded-full max-w-full bg-white dark:bg-gray-800">
          <span className="text-[10px] truncate block">{text}</span>
        </div>
      )}
    </div>
  )
}

function MarkerShapeIcon({ markerShape, color, size }: { markerShape?: MapMarkerShape; color: string; size: number }) {
  switch (markerShape) {
    case MapMarkerShape.INFO:
      return <FaInfo color={color} size={size} />
    case MapMarkerShape.CAR:
      return <FaCar color={color} size={size} />
    case MapMarkerShape.CROSSHAIRS:
      return <FaCrosshairs color={color} size={size} />
    case MapMarkerShape.CAMP:
      return <FaCampground color={color} size={size} />
    case MapMarkerShape.TOWER:
      return <FaBroadcastTower color={color} size={size} />
    case MapMarkerShape.MARKER:
      return <FaMarker color={color} size={size} />
    case MapMarkerShape.HOME:
      return <FaHome color={color} size={size} />
    case MapMarkerShape.PERSON:
      return <FaUser color={color} size={size} />
  }
  return null
}
