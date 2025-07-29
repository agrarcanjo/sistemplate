workspace "<PRODUCT NAME> Infrastructure" {
    model {
        ss = softwareSystem "<PRODUCT NAME>" "<PRODUCT NAME> Infrastructure" {
            aksclusternode = container "AKS Cluster Node" "" "" "Kubernetes - node"
            k8singress = container "Kubernetes Ingress" "" "" "Kubernetes - ing"

            k8singress -> aksclusternode "Route incomming cluster traffic"

            <PLACE_HERE_PRODUCT_CONTAINERS>
        }
        development = deploymentEnvironment "Development" {
            deploymentNode "Subscription" {
                publicadordev = infrastructureNode "Publicador" "" "" "largetext" {
                    description "Receives HTTPS ->\n <ADD_HERE_PRODUCT_URLS_FOR_DEV>\n \nRedirects ->\n - ingress.dev.ic.ama.lan"
                }
                tags "Microsoft Azure - Subscriptions"
            }
            deploymentNode "Development - DEV - Environment" {
                sncicddev = infrastructureNode "spk-DevOps-CICD-route-subnet-dev" "" "" "Microsoft Azure - Route Tables"
                k8singressdev = containerInstance k8singress {
                    description "ingress.dev.ic.ama.lan"
                }
                deploymentNode "AKS Cluster DEV" "" "" "Kubernetes - node" {
                    aksclusternodedev = containerInstance aksclusternode
                    <ADD_HERE_PRODUCT_DEPLOYMENT_NODES_FOR_DEV>
                }
                tags "Microsoft Azure - Resource Groups"
            }
            publicadordev -> k8singressdev "Forwards requests to ingress.dev.ic.ama.lan" "HTTPS" {
                tags "continuousarrowfromoutside"
            }
        }
        test = deploymentEnvironment "Test" {
            deploymentNode "Subscription" {
                publicadortst = infrastructureNode "Publicador" "" "" "largetext" {
                    description "Receives HTTPS ->\n <ADD_HERE_PRODUCT_URLS_FOR_TST>\n \nRedirects ->\n - ingress.tst.ic.ama.lan"
                }
                tags "Microsoft Azure - Subscriptions"
            }
            deploymentNode "Test - TST - Environment" {
                sncicdtst = infrastructureNode "spk-DevOps-CICD-route-subnet-tst" "" "" "Microsoft Azure - Route Tables"
                k8singresstst = containerInstance k8singress {
                    description "ingress.tst.ic.ama.lan"
                }
                deploymentNode "AKS Cluster TST" "" "" "Kubernetes - node" {
                    aksclusternodetst = containerInstance aksclusternode
                    <ADD_HERE_PRODUCT_DEPLOYMENT_NODES_FOR_TST>
                }
                tags "Microsoft Azure - Resource Groups"
            }
            publicadortst -> k8singresstst "Forwards requests to ingress.tst.ic.ama.lan" "HTTPS" {
                tags "continuousarrowfromoutside"
            }
        }
        preproduction = deploymentEnvironment "Pre-Production" {
            deploymentNode "Subscription" {
                publicadorppr = infrastructureNode "Publicador" "" "" "largetext" {
                    description "Receives HTTPS ->\n <ADD_HERE_PRODUCT_URLS_FOR_PPR>\n \nRedirects ->\n - ingress.ppr.ic.ama.lan"
                }
                tags "Microsoft Azure - Subscriptions"
            }
            deploymentNode "Pre-Production - PPR - Environment" {
                sncicdppr = infrastructureNode "spk-DevOps-CICD-route-subnet-ppr" "" "" "Microsoft Azure - Route Tables"
                k8singressppr = containerInstance k8singress {
                    description "ingress.ppr.ic.ama.lan"
                }
                deploymentNode "AKS Cluster PPR" "" "" "Kubernetes - node" {
                    aksclusternodeppr = containerInstance aksclusternode
                    <ADD_HERE_PRODUCT_DEPLOYMENT_NODES_FOR_PPR>
                }
                tags "Microsoft Azure - Resource Groups"
            }
            publicadorppr -> k8singressppr "Forwards requests to ingress.ppr.ic.ama.lan" "HTTPS" {
                tags "continuousarrowfromoutside"
            }
        }
        production = deploymentEnvironment "Production" {
            deploymentNode "Subscription" {
                publicadorprd = infrastructureNode "Publicador" "" "" "largetext" {
                    description "Receives HTTPS ->\n <ADD_HERE_PRODUCT_URLS_FOR_PRD>\n \nRedirects ->\n - ingress.prd.ic.ama.lan"
                }
                tags "Microsoft Azure - Subscriptions"
            }
            deploymentNode "Production - PRD - Environment" {
                sncicdprd = infrastructureNode "spk-DevOps-CICD-route-subnet-prd" "" "" "Microsoft Azure - Route Tables"
                k8singressprd = containerInstance k8singress {
                    description "ingress.prd.ic.ama.lan"
                }
                deploymentNode "AKS Cluster PRD" "" "" "Kubernetes - node" {
                    aksclusternodeprd = containerInstance aksclusternode
                    <ADD_HERE_PRODUCT_DEPLOYMENT_NODES_FOR_PRD>
                }
                tags "Microsoft Azure - Resource Groups"
            }
            publicadorprd -> k8singressprd "Forwards requests to ingress.prd.ic.ama.lan" "HTTPS" {
                tags "continuousarrowfromoutside"
            }
        }
    }
    views {
        deployment ss development {
            title "<PRODUCT NAME> - Development Environment"
            description "Default Kubernetes Cloud DEV environment"
            include *
            autoLayout tb
        }
        deployment ss test {
            title "<PRODUCT NAME> - Test Environment"
            description "Default Kubernetes Cloud TST environment"
            include *
            autoLayout tb
        }
        deployment ss preproduction {
            title "<PRODUCT NAME> - Pre-Production Environment"
            description "Default Kubernetes Cloud PPR environment"
            include *
            autoLayout tb
        }
        deployment ss production {
            title "<PRODUCT NAME> - Production Environment"
            description "Default Kubernetes Cloud PRD environment"
            include *
            autoLayout tb
        }
        themes https://static.structurizr.com/themes/microsoft-azure-2023.01.24/theme.json https://static.structurizr.com/themes/kubernetes-v0.3/theme.json
        styles {
            relationship "continuousarrow" {
                dashed false
                color #808000
            }
            relationship "continuousarrowfromoutside" {
                dashed false
                color #1966FF
            }
            element "filler" {
                width 1
                height 1
                description false
                metadata false
                opacity 100
            }
            element "Deployment Node" {
                metadata true
            }
            element "largetext" {
                width 900
                height 500
            }
        }
    }
}