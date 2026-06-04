import { useConfigContext } from '@/api/contexts/config/ConfigContext'
import { useLocationQuery } from '@/api/hooks/location/useLocationQuery'
import { CmschPage } from '@/common-components/layout/CmschPage'
import { MapContent } from '@/common-components/map/MapContent'
import Markdown from '@/common-components/Markdown'
import { Checkbox } from '@/components/ui/checkbox'
import { Label } from '@/components/ui/label'
import { l } from '@/util/language'
import { X } from 'lucide-react'
import { useState } from 'react'

export default function MapPage() {
  const [showUserLocation, setShowUserLocation] = useState(false)
  const [fullScreen, setFullScreen] = useState(false)
  const locationQuery = useLocationQuery()
  const component = useConfigContext()?.components?.location

  return (
    <CmschPage title={component?.title || 'Térkép'}>
      <h1 className="text-3xl font-bold font-heading">Térkép</h1>
      {component?.topMessage && (
        <div className="mt-5">
          <Markdown text={component.topMessage} />
        </div>
      )}
      <div className="flex flex-wrap items-center gap-4 my-3">
        <div className="flex items-center space-x-2">
          <Checkbox id="show-user-location" checked={showUserLocation} onCheckedChange={(checked) => setShowUserLocation(!!checked)} />
          <Label htmlFor="show-user-location" className="cursor-pointer">
            {l('location-show-own')}
          </Label>
        </div>
        <div className="flex items-center space-x-2">
          <Checkbox id="full-screen" checked={fullScreen} onCheckedChange={(checked) => setFullScreen(!!checked)} />
          <Label htmlFor="full-screen" className="cursor-pointer">
            {'Teljes képernyő'}
          </Label>
        </div>
      </div>
      <div className={fullScreen ? "fixed inset-0 z-50 bg-white" : ""}>
        {fullScreen && (
          <button
            onClick={() => setFullScreen(false)}
            className="absolute top-4 right-4 z-50 bg-black/50 hover:bg-black/70 text-white p-2 rounded-full shadow-lg transition-colors"
            aria-label="Bezárás"
          >
            <X size={24} />
          </button>
        )}
        <MapContent
          mapData={locationQuery.data ?? []}
          showUserLocation={showUserLocation}
          className={fullScreen ? "h-screen w-screen" : ""}
          height={fullScreen ? typeof window !== 'undefined' ? window.innerHeight : 800 : 400}
        />
      </div>
      <p className="mt-4">{l('location-description')}</p>
      <p>{l('location-privacy')}</p>
      {component?.bottomMessage && (
        <div className="pt-4">
          <Markdown text={component.bottomMessage} />
        </div>
      )}
    </CmschPage>
  )
}
