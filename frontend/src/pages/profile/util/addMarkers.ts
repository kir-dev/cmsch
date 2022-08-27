import Feature from 'ol/Feature'
import Point from 'ol/geom/Point'
import { fromLonLat } from 'ol/proj'
import { Circle, Fill, Stroke, Style } from 'ol/style'
import { GroupMemberLocationView } from '../../../util/views/groupMemberLocation.view'

export const addMarkers = (data: GroupMemberLocationView[]) => {
  const stroke = new Stroke({ color: 'white', width: 4 })
  const regularIconStyle = new Style({
    image: new Circle({
      fill: new Fill({ color: '#ef7564' }),
      radius: 10,
      stroke
    })
  })
  const userIconStyle = new Style({
    image: new Circle({
      fill: new Fill({ color: '#172b4d' }),
      radius: 10,
      stroke
    })
  })
  const features = data.map((item) => {
    const feature = new Feature({
      geometry: new Point(fromLonLat([item.longitude, item.latitude]))
    })
    feature.setStyle(item.id === -1 ? userIconStyle : regularIconStyle)
    feature.set('person', item)
    return feature
  })
  return features
}
