# TODOS

~~1. Buscar la descripción del diagnóstico de los pacientes ambulatorios y mostrarlas~~

~~2. Buscar la información de los pacientes que se necesitará luego como edad, sexo y obra social (descrp)~~

~~3. Sincronizar rutas con la historia (pushy?)~~

~~4. Corregir el datasource de los select de operacion realizada y operacion propuesta~~

~~5. Corregir cada select (diagnostico, diagnostico operatorio, operacion propuesta y operacion realizada) para que el valor sea el código (que tendremos que persistir) y se muestre la descripción.~~

~~6. No está funcionando el marcador de carga en la lista de pacientes ~~

7. ~~La hora del membrete debería actualizarse en tiempo real.~~

8. ~~Convertir todos los archivos de modelo a cljc~~

9. Crear grilla

10. Crear formularios para llenado de la ficha propiamente dicha

11. ~~Redirigir /lista_pacientes a index.html para evitar 404 al recargar~~

12. ~~Cuando hay un timeout para cargar recursos (e.g. al armar la lista), debe aparecer el mensaje adecuado en la UI~~
[(com.fulcrologic.fulcro.ui-state-machines/trigger-state-machine-event
  {:com.fulcrologic.fulcro.ui-state-machines/asm-id
   :main.frontend.root/CargaRouter,
   :com.fulcrologic.fulcro.ui-state-machines/event-id :timeout!,
   :com.fulcrologic.fulcro.ui-state-machines/event-data {}})]
12.1 El problema es que el backend está enviando el timeout como una respuesta 200
  - Debería tener en el request la llave del error, de lo contrario, Fulcro lo purga

13. Estamos teniendo el siguiente error de Pathom que no nos permite utilizar el EQL en Fulcro Inspect => "Pathom can't find a path for the following elements in the query: [:com.wsscode.pathom.connect/indexes]",

14. No quiero que la hora cause una recarga del componente padre.

15. Tengo problemas con la normalización de los datos en la base de datos



Plugins:

No supe cómo usarlos bien
```clojure
(p.plugin/register {::p.plugin/id 'mutacion
                                 :com.wsscode.pathom3.connect.runner/wrap-mutate
                                 (fn [mutate]
                                   (fn [env params] 
                                     (try 
                                       (mutate env params)
                                       (catch Exception err {:com.wsscode.pathom3.connect.runner/mutation-error (ex-message err)}))))})
             (p.plugin/register {::p.plugin/id 'resolver
                                 :com.wsscode.pathom3.connect.runner/wrap-resolve
                                 (fn [resolve]
                                   (fn [env params]
                                     (try
                                       (resolve env params)
                                       (catch Exception err {:com.wsscode.pathom3.connect.runner/error (ex-message err)}))))})
             (p.plugin/register {::p.plugin/id 'err
                                 :com.wsscode.pathom3.connect.runner/wrap-resolver-error (fn [_]
                                                                                           (fn [_ node error] 
                                                                                             (let [msj (ex-message error)]
                                                                                               (µ/log ::error-en-resolver-pathom :fecha (t/date-time) :error msj :node node)
                                                                                               {:com.wsscode.pathom3.connect.runner/error msj})))})
             (p.plugin/register {::p.plugin/id 'err-mutation
                                 :com.wsscode.pathom3.connect.runner/wrap-mutation-error
                                 (fn [_]
                                   (fn [_ ast error]
                                     (let [msj (ex-message error)]
                                       (µ/log ::error-en-mutacion-pathom :at (str "Error on" (:key ast)) :exception msj)
                                       {:com.wsscode.pathom3.connect.runner/error msj})))})
```