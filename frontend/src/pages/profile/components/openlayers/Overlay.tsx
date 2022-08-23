import { useContext, useEffect } from 'react'
import MapContext from './MapContext'
import OLOVerlay from 'ol/Overlay'
import { Coordinate } from 'ol/coordinate'

type OverlayProps = { element: HTMLElement | null; position: Coordinate }

const Overlay = ({ element, position }: OverlayProps) => {
  const map = useContext(MapContext)
  useEffect(() => {
    if (!map) return
    if (!element) return

    let ol = new OLOVerlay({ element, position })
    map.addOverlay(ol)
    return () => {
      if (map) {
        map.removeOverlay(ol)
      }
    }
  }, [map, element, position])
  return null
}

export default Overlay
