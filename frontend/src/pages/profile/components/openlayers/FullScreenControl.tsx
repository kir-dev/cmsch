import { FullScreen } from 'ol/control'
import { useContext, useEffect } from 'react'
import MapContext from './MapContext'

const FullScreenControl = () => {
  const map = useContext(MapContext)
  useEffect(() => {
    if (!map) return
    let fullScreenControl = new FullScreen({})
    map.addControl(fullScreenControl)

    return () => {
      if (map) {
        map.removeControl(fullScreenControl)
      }
    }
  }, [map])
  return null
}
export default FullScreenControl
