import { useConfigContext } from '@/api/contexts/config/ConfigContext'
import { useLocationQuery } from '@/api/hooks/location/useLocationQuery'
import { CmschPage } from '@/common-components/layout/CmschPage'
import { MapContent } from '@/common-components/map/MapContent'
import Markdown from '@/common-components/Markdown'
import { Checkbox } from '@/components/ui/checkbox'
import { Label } from '@/components/ui/label'
import { l } from '@/util/language'
import { useState } from 'react'

export default function MapPage() {
  const [showUserLocation, setShowUserLocation] = useState(false)
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
      <div className="flex items-center space-x-2 my-3">
        <Checkbox id="show-user-location" checked={showUserLocation} onCheckedChange={(checked) => setShowUserLocation(!!checked)} />
        <Label htmlFor="show-user-location" className="cursor-pointer">
          {l('location-show-own')}
        </Label>
      </div>
      <MapContent mapData={locationQuery.data ?? []} showUserLocation={showUserLocation} />
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
