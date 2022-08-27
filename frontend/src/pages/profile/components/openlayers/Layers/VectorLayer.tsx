import { useContext, useEffect } from 'react'
import MapContext from '../MapContext'
import OLVectorLayer from 'ol/layer/Vector'
import OLVectorSource from 'ol/source/Vector'
import Style from 'ol/style/Style'

type VectorLayerProps = {
  source: OLVectorSource
  style?: Style
  zIndex?: number
}

const VectorLayer = ({ source, style, zIndex = 0 }: VectorLayerProps) => {
  const map = useContext(MapContext)

  useEffect(() => {
    if (!map) return
    const vectorLayer = new OLVectorLayer({
      source,
      style,
      zIndex
    })
    map.addLayer(vectorLayer)
    return () => {
      if (map) {
        map.removeLayer(vectorLayer)
      }
    }
  }, [map, source])

  return null
}
export default VectorLayer
