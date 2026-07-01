import { useConfigContext } from '@/api/contexts/config/ConfigContext'
import { useLocationQuery } from '@/api/hooks/location/useLocationQuery'
import { Checkbox } from '@/components/ui/checkbox'
import { Label } from '@/components/ui/label'
import { Separator } from '@/components/ui/separator'
import { l } from '@/util/language'
import { useState } from 'react'
import { MapContent } from './MapContent'

function GroupMapContainer() {
  const profileConfig = useConfigContext()?.components?.profile
  const [showUserLocation, setShowUserLocation] = useState(false)
  const locationQuery = useLocationQuery()

  return (
    <div>
      <Separator className="my-10 h-1 bg-border" />
      {profileConfig && <h2 className="text-2xl font-bold my-5">{profileConfig.groupLeadersHeader} pozíciója</h2>}
      <div className="flex items-center space-x-2 my-3">
        <Checkbox id="group-show-user-location" checked={showUserLocation} onCheckedChange={(checked) => setShowUserLocation(!!checked)} />
        <Label htmlFor="group-show-user-location" className="cursor-pointer">
          {l('location-show-own')}
        </Label>
      </div>
      <MapContent mapData={locationQuery.data ?? []} showUserLocation={showUserLocation} />
      <p className="mt-4">{l('location-description')}</p>
      <p>{l('location-privacy')}</p>
    </div>
  )
}

export default GroupMapContainer
